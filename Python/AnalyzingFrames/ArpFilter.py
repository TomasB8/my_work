#trieda, do ktorej sa ukladajú TFTP komunikácie
class ArpFilter:
    def __init__(self, src, dst):               #konštruktor
        self.src = src
        self.dst = dst
        self.requests = []
        self.reply = []

    #metóda na pridanie rámca do poľa requests
    def add_request(self, request):
        self.requests.append(request)

    #metóda na pridanie rámca do poľa reply
    def add_reply(self, reply):
        self.reply.append(reply)

    #metóda kontrolujúca, či sú dve triedy zhodné
    def is_same(self, ArpFilter, opcode):
        if opcode == "REQUEST" and self.dst == ArpFilter.dst:
            return True
        elif opcode == "REPLY" and self.dst == ArpFilter.src:
            return True
        return False  
    
    #metóda, ktorá zisťuje, či prvky req a rep patria do dvojice
    def check_couple(self, rep, req):
        if req['src_ip'] == rep['dst_ip'] and req['dst_ip'] == rep['src_ip']:
            return True
        return False
    
    #funkcia, ktorá kontroluje, či je komunikácia kompletná
    def check_complete_com(self):
        complete = []
        reqs = []
        #for-cyklus, ktorý prechádza všetky requesty
        for i, req in enumerate(self.requests):
            found_couple = False
            #for-cyklus, ktorý prechádza všetky reply
            for j, rep in enumerate(self.reply):
                if self.check_couple(req, rep):             #kontroluje, či tvoria pár
                    found_couple = True
                    complete.append(self.requests[i])
                    complete.append(self.reply.pop(j))
            if not found_couple:        #ak nenájde pár pre req, vloží ho do poľa reqs
                reqs.append(req)

        #metóda vracia tri polia, pole kompletných komunikácii a polia requestov a replyov
        return complete, reqs, self.reply