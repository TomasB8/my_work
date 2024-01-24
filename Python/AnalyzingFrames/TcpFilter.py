#trieda, do ktorej sa ukladajú TCP komunikácie
class TcpFilter:
    def __init__(self, src, dst, src_port, dst_port):       #konštruktor
        self.src = src
        self.dst = dst
        self.src_port = src_port
        self.dst_port = dst_port
        self.frames = []
        self.flags = []

    #definovanie, kedy sú dve triedy zhodné
    def __eq__(self, TcpFilter):
        if TcpFilter.src==self.src and TcpFilter.dst==self.dst and TcpFilter.src_port==self.src_port and TcpFilter.dst_port==self.dst_port:
            return True
        elif TcpFilter.src==self.dst and TcpFilter.dst==self.src and TcpFilter.src_port==self.dst_port and TcpFilter.dst_port==self.src_port:
            return True
        return False

    #metóda na pridanie rámca do komunikácie
    def add_frame(self, frame):
        self.frames.append(frame)

    #metóda na pridanie príznakov do poľa v komunikácii
    def add_flag(self, flag):
        self.flags.append(flag)
    
    #funkcia, ktorá zisťuje, či v jednej komunikácii neprebehlo otvorenie a zatvorenia viackrát
    def check_more_comms(self):
        syn_counter = 0
        syn_order = 0
        fin_counter = 0
        #spočíta počet FIN a SYN flagov
        for i in range(len(self.flags)):
            if self.flags[i]['SYN']:
                syn_counter += 1
                if syn_counter == 3:
                    syn_order = i
            elif self.flags[i]['FIN']:
                fin_counter += 1

        #ak je počet SYN alebo FIN flagov viac ako 2, tak otvorenie a zatvorenie prebehlo viackrát
        if syn_counter > 2 or fin_counter > 2:
            new_tcp = TcpFilter(self.src, self.dst, self.src_port, self.dst_port)
            for j in range(syn_order, len(self.frames)):
                new_tcp.add_frame(self.frames[j])
                new_tcp.add_flag(self.flags[j])
            self.frames = self.frames[:syn_order]
            return new_tcp
        return None
    
    #funkcia, ktorá kontroluje, či je komunikácia kompletná
    def check_complete_com(self):
        opened = False
        closed = False
        
        try:
            #najskôr pozrie na posledný flag, ak je tam príznak RST, považuje komunikáciu za uzavretú
            if self.flags[-1]['RST']:
                closed = True

            #for-cyklus, ktorý prechádza pole príznakov a kontroluje jednotlivé scenáre otvorenia a zatvorenia komunikácie
            for i in range(len(self.flags)):

                if(self.flags[i]['SYN']) and (self.frames[i]['src_port'] == self.src_port and self.frames[i]['dst_port'] == self.dst_port):
                    if(self.flags[i+1]['ACK'] and self.flags[i+1]['SYN']) and (self.frames[i+1]['src_port'] == self.dst_port and self.frames[i+1]['dst_port'] == self.src_port):
                        if self.flags[i+2]['ACK'] and (self.frames[i+2]['src_port'] == self.src_port and self.frames[i+2]['dst_port'] == self.dst_port):
                            opened = True
                            i += 2
                
                if(self.flags[i]['SYN']) and (self.frames[i]['src_port'] == self.src_port and self.frames[i]['dst_port'] == self.dst_port):
                    if(self.flags[i+1]['SYN']) and (self.frames[i+1]['src_port'] == self.dst_port and self.frames[i+1]['dst_port'] == self.src_port):
                        if self.flags[i+2]['ACK'] and (self.frames[i+2]['src_port'] == self.dst_port and self.frames[i+2]['dst_port'] == self.src_port):
                            if self.flags[i+3]['ACK'] and (self.frames[i+3]['src_port'] == self.src_port and self.frames[i+3]['dst_port'] == self.dst_port):
                                opened = True
                                i += 3

                #ak už bola zatvorená (na konci je RST príznak), nasledujúci blok kódu sa nevykoná
                if not closed:
                    if(self.flags[i]['FIN'] and self.flags[i]['ACK']) and (self.frames[i]['src_port'] == self.src_port and self.frames[i]['dst_port'] == self.dst_port):
                        if(self.flags[i+1]['ACK'] and not self.flags[i+1]['FIN']) and (self.frames[i+1]['src_port'] == self.dst_port and self.frames[i+1]['dst_port'] == self.src_port):
                            if(self.flags[i+2]['FIN'] and self.flags[i+2]['ACK']) and (self.frames[i+2]['src_port'] == self.dst_port and self.frames[i+2]['dst_port'] == self.src_port):
                                if self.flags[i+3]['ACK'] and (self.frames[i+3]['src_port'] == self.src_port and self.frames[i+3]['dst_port'] == self.dst_port):
                                    closed = True
                                    break
                                else:
                                    for j in range(i+3, len(self.flags)):
                                        if self.flags[j]['ACK'] and (self.frames[j]['src_port'] == self.src_port and self.frames[j]['dst_port'] == self.dst_port):
                                            closed = True
                                            break
                                    break
                            elif(self.flags[i+2]['RST']):
                                closed = True
                                break
                            else:
                                for j in range(i+2, len(self.flags)):
                                    if(self.flags[j]['FIN'] and self.flags[j]['ACK']) and (self.frames[j]['src_port'] == self.dst_port and self.frames[j]['dst_port'] == self.src_port):
                                        if self.flags[j+1]['ACK'] and (self.frames[j+1]['src_port'] == self.src_port and self.frames[j+1]['dst_port'] == self.dst_port):
                                            closed = True
                                            break
                                break

                    if(self.flags[i]['FIN'] and self.flags[i]['ACK']) and (self.frames[i]['src_port'] == self.dst_port and self.frames[i]['dst_port'] == self.src_port):
                        if(self.flags[i+1]['ACK'] and not self.flags[i+1]['FIN']) and (self.frames[i+1]['src_port'] == self.src_port and self.frames[i+1]['dst_port'] == self.dst_port):
                            if(self.flags[i+2]['FIN'] and self.flags[i+2]['ACK']) and (self.frames[i+2]['src_port'] == self.src_port and self.frames[i+2]['dst_port'] == self.dst_port):
                                if self.flags[i+3]['ACK'] and (self.frames[i+3]['src_port'] == self.dst_port and self.frames[i+3]['dst_port'] == self.src_port):
                                    closed = True
                                    break
                                else:
                                    for j in range(i+3, len(self.flags)):
                                        if self.flags[j]['ACK'] and (self.frames[j]['src_port'] == self.dst_port and self.frames[j]['dst_port'] == self.src_port):
                                            closed = True
                                            break
                                    break
                            elif(self.flags[i+2]['RST']):
                                closed = True
                                break
                            else:
                                for j in range(i+2, len(self.flags)):
                                    if(self.flags[j]['FIN'] and self.flags[j]['ACK']) and (self.frames[j]['src_port'] == self.src_port and self.frames[j]['dst_port'] == self.dst_port):
                                        if self.flags[j+1]['ACK'] and (self.frames[j+1]['src_port'] == self.dst_port and self.frames[j+1]['dst_port'] == self.src_port):
                                            closed = True
                                            break
                                break

                    if(self.flags[i]['FIN']) and (self.frames[i]['src_port'] == self.src_port and self.frames[i]['dst_port'] == self.dst_port):
                        if(self.flags[i+1]['ACK']) and (self.frames[i+1]['src_port'] == self.dst_port and self.frames[i+1]['dst_port'] == self.src_port):
                            if(self.flags[i+2]['FIN']) and (self.frames[i]['src_port'] == self.dst_port and self.frames[i]['dst_port'] == self.src_port):
                                if self.flags[i+3]['ACK'] and (self.frames[i+3]['src_port'] == self.src_port and self.frames[i+3]['dst_port'] == self.dst_port):
                                    closed = True
                                    break

                    if(self.flags[i]['FIN'] and self.flags[i]['ACK']) and (self.frames[i]['src_port'] == self.src_port and self.frames[i]['dst_port'] == self.dst_port):
                        if(self.flags[i+1]['FIN'] and self.flags[i+1]['ACK']) and (self.frames[i+1]['src_port'] == self.dst_port and self.frames[i+1]['dst_port'] == self.src_port):
                            closed = True
                            break

                    if(self.flags[i]['FIN'] and self.flags[i]['ACK']) and (self.frames[i]['src_port'] == self.dst_port and self.frames[i]['dst_port'] == self.src_port):
                        if(self.flags[i+1]['FIN'] and self.flags[i+1]['ACK']) and (self.frames[i+1]['src_port'] == self.src_port and self.frames[i+1]['dst_port'] == self.dst_port):
                            closed = True
                            break

                    if(self.flags[i]['FIN']) and (self.frames[i]['src_port'] == self.src_port and self.frames[i]['dst_port'] == self.dst_port):
                        if(self.flags[i+1]['RST']) and (self.frames[i+1]['src_port'] == self.dst_port and self.frames[i+1]['dst_port'] == self.src_port):
                            closed = True
                            break

                    if(self.flags[i]['RST']) and (self.frames[i]['src_port'] == self.dst_port and self.frames[i]['dst_port'] == self.src_port):
                        closed = True
                        break

                    if(self.flags[i]['RST']) and (self.frames[i]['src_port'] == self.src_port and self.frames[i]['dst_port'] == self.dst_port):
                        closed = True
                        break

        except IndexError:
            return False
        
        #ak bola komunikácia otvorená aj zatvorená, funkcia bráti True
        if opened and closed:
            return True
        
        #inak vráti False
        return False