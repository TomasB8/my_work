#trieda reprezentujúca fragment jednej fragmentovanej komunikácie
class Fragment:
    def __init__(self, src, dst, id, icmp_type, icmp_id, icmp_seq):         #konštruktor
        self.src = src
        self.dst = dst
        self.id = id
        self.icmp_type = icmp_type
        self.icmp_id = icmp_id
        self.icmp_seq = icmp_seq
        self.frames = []

    #definovanie, kedy sú dve triedy zhodné
    def __eq__(self, Fragment):
        if Fragment.src==self.src and Fragment.dst==self.dst and Fragment.id==self.id:
            return True
        return False

    #metóda na pridanie rámca do fragmentu
    def add_frame(self, frame):
        self.frames.append(frame)


#trieda, do ktorej sa ukladajú fragmentované komunikácie
class FragmentedICMP:
    def __init__(self):                 #konštruktor
        self.fragments = []

    #metóda na pridanie fragmentu do komunikácie
    def add_fragment(self, fragment):
        self.fragments.append(fragment)

    #metóda, ktorá zisťuje, či fragment patrí do danej fragmentovanej komunikácie
    def is_same(self, fragment):
        for frag in self.fragments:
            if frag.src==fragment.dst and frag.dst==fragment.src and frag.icmp_id==fragment.icmp_id:
                return True
        return False

    #metóda, ktorá zisťuje, či sa fragment v danej komunikácii už nachádza
    def has_fragment(self, fragment):
        for frag in self.fragments:
            if frag.src==fragment.src and frag.dst==fragment.dst and frag.id==fragment.id:
                return True
        return False
    
    #metóda, ktorá vráti pozíciu zadaného fragmentu v poli komunikácie
    def find_fragment(self, fragment):
        for i, frag in enumerate(self.fragments):
            if frag.src==fragment.src and frag.dst==fragment.dst and frag.id==fragment.id:
                return i
        return -1
    
    #funkcia, ktorá kontroluje, či je komunikácia kompletná
    def check_complete_com(self):
        req_sent = False
        rep_sent = False
        sequence = 0
        #for-cyklus, ktorý prechádza všetky fragmenty fragmentovanej komunikácie
        for i in range(0, len(self.fragments)):
            #na základe icmp_type sa posúva do jednotlivých stavov a kontroluje, či je komunikácia kompletná
            if 'icmp_type' in self.fragments[i].frames[1]:
                if self.fragments[i].frames[1]['icmp_type'].upper() == "Echo request".upper():
                    sequence = self.fragments[i].frames[1]['icmp_seq']
                    req_sent = True
                    rep_sent = False
                elif req_sent and self.fragments[i].frames[1]['icmp_type'].upper() == "Echo reply".upper() and sequence == self.fragments[i].frames[1]['icmp_seq']:
                    rep_sent = True
                    req_sent = False
                elif req_sent and self.fragments[i].frames['icmp_type'].upper() == "Time exceeded".upper() and sequence == self.fragments[i].frames['icmp_seq']:
                    rep_sent = True
                    req_sent = False
                else:
                    return False
        return True


#trieda, do ktorej sa ukladajú nefragmentované ICMP komunikácie
class ICMP:
    def __init__(self, src, dst, id = -1):          #konštruktor
        self.src = src
        self.dst = dst
        self.id = id
        self.frames = []

    #definovanie, kedy sú dve triedy zhodné
    def __eq__(self, ICMP):
        if self.id != -1 and ICMP.src==self.src and ICMP.dst==self.dst and ICMP.id==self.id:
            return True
        elif self.id != -1 and ICMP.src==self.dst and ICMP.dst==self.src and ICMP.id==self.id:
            return True
        return False
    
    #funkcia, ktorá kontroluje, či rámec s icmp_type = time exceeded, patrí do tejto komunikácie
    def is_same(self, data):
        if 'icmp_type' in data and self.id != -1 and self.src == data['dst_ip'] and data['icmp_type'].lower() == "time exceeded":
            return True
        return False

    #metóda na pridanie rámca do komunikácie
    def add_frame(self, frame):
        self.frames.append(frame)
    
    #funkcia, ktorá kontroluje, či je komunikácia kompletná
    def check_complete_com(self):
        req_sent = False
        rep_sent = False
        sequence = 0
        #for-cyklus, ktorý prechádza všetky rámce komunikácie
        for i, frame in enumerate(self.frames):
            #kontroluje striedanie Echo request a Echo reply / Time exceeded
            if i%2 == 0:
                if 'icmp_type' in frame and frame['icmp_type'].upper() == "Echo request".upper():
                    sequence = frame['icmp_seq']
                    req_sent = True
                    rep_sent = False
                #v prípade nesprávneho poradia vráti False
                else:
                    return False
            else:
                if req_sent and frame['icmp_type'].upper() == "Echo reply".upper() and sequence == frame['icmp_seq']:
                    rep_sent = True
                    req_sent = False
                elif req_sent and frame['icmp_type'].upper() == "Time exceeded".upper() and sequence == frame['icmp_seq']:
                    rep_sent = True
                    req_sent = False
                #v prípade nesprávneho poradia vráti False
                else:
                    return False
        return True