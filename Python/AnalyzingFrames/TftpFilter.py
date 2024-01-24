#trieda, do ktorej sa ukladajú TFTP komunikácie
class TftpFilter:
    def __init__(self, src, dst, src_port, dst_port):           #konštruktor
        self.src = src
        self.dst = dst
        self.src_port = src_port
        self.dst_port = dst_port
        self.size = -1
        self.frames = []
        self.opcodes = []

    #definovanie, kedy sú dve triedy zhodné
    def __eq__(self, TFTP):
        if TFTP.src==self.src and TFTP.dst==self.dst and TFTP.src_port==self.src_port and TFTP.dst_port==self.dst_port:
            return True
        elif TFTP.src==self.dst and TFTP.dst==self.src and TFTP.src_port==self.dst_port and TFTP.dst_port==self.src_port:
            return True
        elif TFTP.src==self.dst and TFTP.dst==self.src and TFTP.dst_port==self.src_port:
            return True
        return False
    
    def get_normal_size(self):
        found = False
        for i in range(len(self.frames)):
            if self.opcodes[0] == 2:
                self.size = 512
                found = True
                break
            elif(self.opcodes[i] == 3) and self.opcodes[0] == 1:
                self.size = self.frames[i]['len_frame_pcap']
                found = True
                break
        
        if not found:
            self.size = -1

    #metóda na pridanie rámca do komunikácie
    def add_frame(self, frame):
        if(self.size == -1):
            self.get_normal_size()
        if len(self.frames) == 1:
            self.dst_port = frame['src_port']
        self.frames.append(frame)

    #metóda na pridanie opcode do poľa v komunikácii
    def add_opcode(self, opcode):
        self.opcodes.append(opcode)
    
    #funkcia, ktorá kontroluje, či je komunikácia kompletná
    def check_complete_com(self):
        opened = False
        closed = False
        closing = False
        data_sent = False
        ack_sent = False
        #for-cyklus, ktorý prechádza všetky rámce v komunikácii
        for i, frame in enumerate(self.frames):
            
            #špeciálne kontroluje prvý rámec, ako otvorenie TFTP komunikácie
            if i == 0 and self.opcodes[i] == 1 and (frame['src_port'] == self.src_port and frame['dst_port'] == 69):
                opened = True
            elif i == 0 and self.opcodes[i] == 2 and (frame['src_port'] == self.src_port and frame['dst_port'] == 69):
                opened = True
            else:
                if self.opcodes[0] == 1:
                    #pozerá posledný rámec, ktorý musí byť acknowledgment po tom, čo bol poslaný posledný rámec s dátami
                    if closing and self.opcodes[i] == 4 and data_sent and (frame['src_port'] == self.src_port and frame['dst_port'] == self.dst_port):
                        ack_sent = True
                        closed = True
                        break
                    elif self.opcodes[i] == 5 and data_sent and (frame['src_port'] == self.src_port and frame['dst_port'] == self.dst_port):
                        ack_sent = True
                        closed = True
                    #kontrolovanie dĺžky rámca, ak je menší ako dohodnutá dĺžka, je to posledný rámec s dátami
                    if self.opcodes[i] == 3 and frame['len_frame_pcap'] < self.size:
                        closing = True
                        data_sent = True
                        #next_frame = self.frames[i+1]
                    else:
                        #na striedačku kontroluje, či boli poslané dáta a následne acknowledgment
                        if self.opcodes[i] == 3 and (frame['src_port'] == self.dst_port and frame['dst_port'] == self.src_port):
                            data_sent = True
                            ack_sent = False
                        elif self.opcodes[i] == 5 and (frame['src_port'] == self.src_port and frame['dst_port'] == self.dst_port):
                            ack_sent = True
                            data_sent = True
                            closed = True
                            break
                        elif self.opcodes[i] == 4 and data_sent and (frame['src_port'] == self.src_port and frame['dst_port'] == self.dst_port):
                            ack_sent = True
                            data_sent = False

                elif self.opcodes[0] == 2:
                    #pozerá posledný rámec, ktorý musí byť acknowledgment po tom, čo bol poslaný posledný rámec s dátami
                    if closing and self.opcodes[i] == 4 and data_sent and (frame['src_port'] == self.dst_port and frame['dst_port'] == self.src_port):
                        ack_sent = True
                        closed = True
                        break
                    elif self.opcodes[i] == 5 and data_sent and (frame['src_port'] == self.dst_port and frame['dst_port'] == self.src_port):
                        ack_sent = True
                        closed = True
                    if self.opcodes[i] == 3 and frame['len_frame_pcap'] < self.size:
                        closing = True
                        data_sent = True
                        #next_frame = self.frames[i+1]
                    else:
                        #na striedačku kontroluje, či boli poslané dáta a následne acknowledgment
                        if self.opcodes[i] == 3 and (frame['src_port'] == self.src_port and frame['dst_port'] == self.dst_port):
                            data_sent = True
                            ack_sent = False
                        elif self.opcodes[i] == 5 and (frame['src_port'] == self.dst_port and frame['dst_port'] == self.src_port):
                            ack_sent = True
                            data_sent = True
                            closed = True
                            break
                        elif self.opcodes[i] == 4 and (frame['src_port'] == self.dst_port and frame['dst_port'] == self.src_port):
                            ack_sent = True
                            data_sent = False


        #ak prebehlo všetko ako má, funkcia vráti True
        if opened and closed and data_sent and ack_sent:
            return True
        #inak False
        return False