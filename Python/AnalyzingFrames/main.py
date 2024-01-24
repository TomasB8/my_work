from scapy.all import rdpcap
import ruamel.yaml
import sys
from ArpFilter import ArpFilter
from IcmpFilter import ICMP, FragmentedICMP, Fragment
from TftpFilter import TftpFilter
from TcpFilter import TcpFilter


#načítanie súboru na analýzu zo štandardného vstupu
#file_name = input("Zadajte súbor na analýzu: ")
file_name = "pks-all.pcap"
#file_name = "trace-26.pcap"
pcap_file = f"vzorky_pcap_na_analyzu/{file_name}"
#pokus o načítanie dát zo súboru, prípadne ukončenie programu, ak sa to nepodarí
try:
    packets = rdpcap(pcap_file)
except FileNotFoundError:
    print(f"Súbor {file_name} sa nepodarilo nájsť.")
    sys.exit(0)
except IsADirectoryError:
    print(f"Nesprávny názov súboru.")
    sys.exit(0)

#slovníky pre LLC a PID
EthType = {}
LLC = {}
IP_pr = {}
TCP = {}
UDP = {}
PID = {}
ICMP_types = {}

#funkcia, ktorá naplní slovníky dátami načítanými zo súborov
def load_dict():
    with open("Protocols/EtherType.txt", "r") as f:
        for row in f:
            hexa, name = row.split(":")
            EthType[hexa] = name.strip()

    with open("Protocols/LLC.txt", "r") as f:
        for row in f:
            hexa, name = row.split(":")
            LLC[hexa] = name.strip()
    
    with open("Protocols/ip_protocols.txt", "r") as f:
        for row in f:
            hexa, name = row.split(":")
            IP_pr[hexa] = name.strip()

    with open("Protocols/TCP.txt", "r") as f:
        for row in f:
            hexa, name = row.split(":")
            TCP[hexa] = name.strip()

    with open("Protocols/UDP.txt", "r") as f:
        for row in f:
            hexa, name = row.split(":")
            UDP[hexa] = name.strip()

    with open("Protocols/PID.txt", "r") as f:
        for row in f:
            hexa, name = row.split(":")
            PID[hexa] = name.strip()

    with open("Protocols/ICMP_types.txt", "r") as f:
        for row in f:
            hexa, name = row.split(":")
            ICMP_types[hexa] = name.strip()

#funkcia, ktorá nájde SAP pre IEEE 802.3 LLC
def LLC_get_SAP(x):
    if x in list(LLC.keys()):
        return LLC[x]
    else:
        return ""
    
#funkcia, ktorá nájde SAP pre IEEE 802.3 LLC & SNAP
def SNAP_get_PID(x):
    if x in list(PID.keys()):
        return PID[x]
    else:
        return ""

#funkcia, ktorá vracia bajty v hexadecimálnom tvare, uložené v poli
def get_hex_numbers(packet):
    hex_numbers = list()
    for byte in packet:
        hex_numbers.append(f"{byte:02X}") 
    
    return hex_numbers

#funkcia na výpis hexa_framu
def get_hex_table(hex_numbers):
    result = '|\n'
    for i in range(1, len(hex_numbers)+1):
        result += ' ' + hex_numbers[i-1]
        if i%16 == 0:
            result += '\n'

    return result + "\n"

#funkcia, ktorá nájde cieľovú MAC adresu
def get_dst_addr(packet):
    return ":".join(packet[:6])

#funkcia, ktorá nájde zdrojovú MAC adresu
def get_src_addr(packet):
    return ":".join(packet[6:12])

#funkcia, ktorá nájde typ framu
def get_frame_type(packet, data):
    frametype = "".join(packet[12:14])          #izolovanie ethtype
    #ak je väčšie ako 1500 je to typ ETHERNET II
    if(int("0x"+frametype, 16) > 1500):
        data['frame_type'] = "ETHERNET II"
    else:
        #ak sa na 15. a 16. mieste nachádza FFFF, je to IEEE 802.3 RAW
        if("".join(packet[14:16]) == "FFFF"):
            data['frame_type'] = "IEEE 802.3 RAW"
        #ak sa na 15. pozícii nachádza AA, je to IEEE 802.3 LLC & SNAP
        elif(packet[14:15][0] == "AA"):
            data['frame_type'] = "IEEE 802.3 LLC & SNAP"
            pid = "".join(packet[20:22])            #nájdenie pid
            data['pid'] = SNAP_get_PID(pid)         #zistenie pid
            if(data['pid'] == "Unknown"):           #ak bolo pid neznáme, tak tento rámec obsahuje ISL header a pid sa nachádza na 47. a 48. pozícii
                pid = "".join(packet[46:48])
                data['pid'] = SNAP_get_PID(pid)
        #inak je to IEEE 802.3 LLC
        else:
            data['frame_type'] = "IEEE 802.3 LLC"
            data['sap'] = LLC_get_SAP(packet[15:16][0])     #zistenie sap

#funkcia, ktorá zisťuje ether_type
def get_ether_type(packet):
    ether_type = "".join(packet[12:14])
    if ether_type in EthType:
        return EthType[ether_type]
    
#funkcia, ktorá zisťuje zdrojovú a cieľovú IP adresu
def get_ip_addresses(packet, data):
    #IP adresy c prípade, že ether_type je IPv4
    if (data['ether_type'] == "IPv4"):
        src_ip = ".".join([str(int("0x"+cislo, 16)) for cislo in packet[26:30]])
        dst_ip = ".".join([str(int("0x"+cislo, 16)) for cislo in packet[30:34]])
        return (src_ip, dst_ip)
    #IP adresy v prípade, že ether_type je ARP
    elif (data['ether_type'] == "ARP"):
        src_ip = ".".join([str(int("0x"+cislo, 16)) for cislo in packet[28:32]])
        dst_ip = ".".join([str(int("0x"+cislo, 16)) for cislo in packet[38:42]])
        return (src_ip, dst_ip)
    return ("", "")         #vráti prázdne stringy v prípade neúspechu

#funkcia, ktorá zisťuje protokol na transportnej vrstve
def get_protocol(packet):
    protocol = str(int("0x"+packet[23:24][0], 16))
    if protocol in IP_pr:
        return IP_pr[protocol]
    return ""       #vráti prázdny string v prípade neúspechu

#funkcia, ktorá vracia zdrojový a cieľový port
def get_ports(packet, o):
    src_port = int("".join(packet[34+o:36+o]), 16)              #zdrojový port
    dst_port = int("".join(packet[36+o:38+o]), 16)              #cieľový port
    return (src_port, dst_port)

#funkcia kontrolujúca, či niektorý z portov je jeden zo známych portov
def check_known_port(port, protocol):
    port = str(port)
    if protocol == "TCP":                           #pre protokol TCP
        if port in list(TCP.keys()):
            return TCP[port]
    
    elif protocol == "UDP":                         #pre protokol UDP
        if port in list(UDP.keys()):
            return UDP[port]
        
    return ""

#funkcia, ktorá očistí dáta od hodnôt s prázdnymi reťazcami
def clear_data(data):
    new_data = {}                       #slovník s novými dátami
    for key in data:
        if data[key] != "":             #ak sú nejaké dáta prázdne, nevložia sa do súboru
            new_data[key] = data[key]

    return new_data

#funkcia, ktorá zisťuje príznaky nastavené v IP hlavičke
def get_flags(packet, o):
    hex_flag = "".join(packet[46+o:48+o])[1:]   #načítanie bytov s flagmi a odstránenie prvého znaku (veľkosť hlavičky)
    flags = {                                   #slovník všetkých príznakov
        'Reserved': 0,
        'Accurate ECN': 0,
        'Congestion Window Reduced': 0,
        'ECN-Echo': 0,
        'Urgent': 0,
        'ACK': 0,
        'PSH': 0,
        'RST': 0,
        'SYN': 0,
        'FIN': 0
    }

    #pole názvov príznakov v rovnakom poradí
    fs = ['Reserved','Accurate ECN','Congestion Window Reduced','ECN-Echo','Urgent','ACK','PSH','RST','SYN','FIN']
    i = 0
    #pre každé číslo vo flagoch
    for num in hex_flag:
        j = 0
        bin_num = f"{int(bin(int('0x' + num, 16)).replace('0b', '')):04d}"          #prepis reprezentácie príznakov z hexadecimálnej do binárnej sústavy
        while j < 4:
            if i == 0:
                j = 2                       #preskočenie prvých troch znakov pri príznaku Reserved
            flags[fs[i]] = int(bin_num[j])  #uloženie získaného čísla do prislúchajúceho príznaku
            i += 1
            j += 1
            
    return flags            #vrátenie slovníka s príznakmi

#funkcia, ktorá vráti opcode pre TFTP správy
def get_opcode(packet, o):
    return int("0x" + "".join(packet[42+o:44+o]), 16)

#funkcia, ktorá vráti icmp dáta pre protokol ICMP
def get_icmp(packet, o):
    icmp_type = ""
    x = f"{int('0x' + ''.join(packet[34+o:35+o]), 16):02}"          #načítanie príslušných bytov pre icmp_type
    #ak pozná takýto typ, uloží ho do premennej
    if(x in ICMP_types):
        icmp_type = ICMP_types[x]
    #v prípade, že x je reply alebo request, načíta aj icmp_id a icmp_seq
    if(x == "00" or x == "08"):
        icmp_id = int("0x" + "".join(packet[38+o:40+o]), 16)
        icmp_seq = int("0x" + "".join(packet[40+o:42+o]), 16)
    #takisto v prípade, že x je Time exceeded, načíta aj icmp_id a icmp_seq
    elif(x == "11"):
        icmp_id = int("0x" + "".join(packet[66+o:68+o]), 16)
        icmp_seq = int("0x" + "".join(packet[68+o:70+o]), 16)
    #inak vráti przdne stringy
    else:
        icmp_id = ""
        icmp_seq = ""

    return (icmp_type, icmp_id, icmp_seq)

#funkcia, ktorá vráti icmp dáta pre protokol ICMP, kde sú správy fragmentované
def get_fragmented_icmp(packet, o):
    icmp_type = ""
    x = f"{int('0x' + ''.join(packet[34+o]), 16):02}"                   #načítanie príslušných bytov pre icmp_type
    #ak pozná takýto typ, uloží ho do premennej
    if(x in ICMP_types):
        icmp_type = ICMP_types[x]
    #v prípade, že x je reply alebo request, načíta aj icmp_id a icmp_seq
    if(x == "00" or x == "08"):
        icmp_id = int("0x" + "".join(packet[38+o:40+o]), 16)
        icmp_seq = int("0x" + "".join(packet[40+o:42+o]), 16)
    return (icmp_type, icmp_id, icmp_seq)

#funkcia, ktorá zisťuje, či má daný paket viacej fragmentov
def has_more_fragments(hex_num):
    if hex_num[20] == "20":
        return True
    return False

#funkcia, ktorá vracia frag_offset pre fragmentované správy
def get_frag_offset(hex_num):
    return 8*int("0x" + hex_num[21], 16)

#funkcia, ktorá vracia ID pre fragmentované správy
def get_frag_id(hex_num):
    return int("".join(hex_num[18:20]), 16)

#funkcia, ktorá vypočíta a vráti offset pre IP hlavičky
def calculate_offset(hex_num):
    x = int("0x" + hex_num[14][1], 16)
    return (x*32)//8 - 20



load_dict()             #načítanie dát do slovníkov

#načítanie argumentov zo vstupu
args = sys.argv
in_protocol = "all"             #default hodnota v prípade, že na vstupe nie je zadaný žiadny protokol
if len(args) > 1:
    if(args[1] == "-p"):            #na druhej pozícií musí byť prepínač -p
        try:
            #kontrola, aký protokol bol zadaný na vstupe
            if (args[2].lower() in ['http', 'https', 'telnet', 'ssh', 'ftp-control', 'ftp-data']): 
                in_protocol = args[2]
            elif args[2].lower() in ['tftp']:       
                in_protocol = args[2]
            elif (args[2].upper() == "ICMP"):
                in_protocol = args[2].upper()
            elif (args[2].upper() == "ARP"):
                in_protocol = args[2].upper()
            elif (args[2].upper() == "CDP"):
                in_protocol = args[2].upper()
            #v prípade neznámeho protokolu sa vypíše chybová hláška a program skončí
            else:
                print("Neznámy protokol.")
                sys.exit(0)
        except IndexError:
            print("Neznámy protokol.")
            sys.exit(0)
    else:
        print("Neznámy protokol.")
        sys.exit(0)

#otvorenie súboru, do ktorého sa bude zapisovať výstup
with open("packets.yaml", "w") as file:
    #vytvorenie hlavičky pre výstup
    big_data = {
        'name': 'PKS2023/24',
        'pcap_name': f"{file_name}",
        'filter_name': "",
        'complete_comms': [],
        'partial_comms': [],
        'packets': [],
        'ipv4_senders': [],
        'max_send_packets_by': []
    }

    #pomocné premenné pre filtrovanie
    ip_addresses = {}
    fil = []
    cdp_filter = []
    incomplete = []
    fragmented = False
    fragments = FragmentedICMP()

    #for-cyklus, ktorý prechádza všetky pakety
    for i, packet in enumerate(packets):
        #vytvorenie šablóny pre dáta
        data = {
            'frame_number': "",
            'len_frame_pcap': "",
            'len_frame_medium': "",
            'frame_type': "",
            'src_mac': "",
            'dst_mac': "",
            'sap': "",
            'pid': "",
            'ether_type': "",
            'arp_opcode': "",
            'src_ip': "",
            'dst_ip': "",
            'id': "",
            'flags_mf': "",
            'frag_offset': "",
            'protocol': "",
            'icmp_type': "",
            'icmp_id': "",
            'icmp_seq': "",
            'src_port': "",
            'dst_port': "",
            'app_protocol': "",
            'hexa_frame': "",
        }
        offset = 0                                      #offset pre IP hlavičku
        hex_num = get_hex_numbers(bytes(packet))        #vytvorenie pola bajtov
        length = len(packet)                            #dĺžka rámca
        
        yaml = ruamel.yaml.YAML()                       #inicializácia yaml
        data['frame_number'] = i+1
        data['len_frame_pcap'] = length

        #výpočet dĺžky rámca prenášaného po médiu
        if length+4 < 64:
            data['len_frame_medium'] = 64
        else:
            data['len_frame_medium'] = length+4

        data['src_mac'] = get_src_addr(hex_num)                     #uloženie zdrojovej MAC adresy
        data['dst_mac'] = get_dst_addr(hex_num)                     #uloženie cieľovej MAC adresy
        data['hexa_frame'] = yaml.load(get_hex_table(hex_num))      #načítanie hexa_frame
        
        get_frame_type(hex_num, data)                   #zistenie typu rámca
        
        #ak je to ETHERNET II, zisťuje sa ether_type
        if(data['frame_type'] == 'ETHERNET II'):
            data['ether_type'] = get_ether_type(hex_num)

        #získanie zdrojovej a cieľovej IP adresy
        ips = get_ip_addresses(hex_num, data)
        data['src_ip'] = ips[0]
        data['dst_ip'] = ips[1]
        if(data['src_ip'] != ""):
            #počítadlo zdrojových IP adries pre výpis počtu paketov poslaných jednotlivými IP adresami
            if(data['src_ip'] not in ip_addresses):
                ip_addresses[data['src_ip']] = 1
            else:
                ip_addresses[data['src_ip']] += 1

        #ak je ether_type IPv4, zistí sa protokol na transportnej vsrtve a vypočíta sa offset 
        if(data['ether_type'] == "IPv4"):
            data['protocol'] = get_protocol(hex_num)
            offset = calculate_offset(hex_num)
        #ak je ether_type ARP, získa sa arp_opcode
        elif(data['ether_type'] == "ARP"):
            arp_opcode = int("0x" + "".join(hex_num[20:22]), 16)
            if arp_opcode == 1:
                data['arp_opcode'] = "REQUEST"
            elif arp_opcode == 2:
                data['arp_opcode'] = "REPLY"

        app_protocol = ""                           #premenná, kde sa uloží protokol
        if(data['protocol'] in ['TCP', 'UDP']):     #ak je protokol na transportnej vrstve TCP alebo UDP vykoná sa nasledujúci blok
            #načíta sa zdrojový a cieľový port
            ports = get_ports(hex_num, offset)
            data['src_port'] = ports[0]
            data['dst_port'] = ports[1]
            #obidva porty sa skontrolujú, či niektorý z nich nepatrí medzi známe porty
            for port in ports:
                if data['app_protocol'] == "":          #ak ešte aplikačný protokol nie je známy
                    app_protocol = check_known_port(port, data['protocol'])
                    data['app_protocol'] = app_protocol
        elif(data['protocol'] == "ICMP"):           #ak je protokol ICMP
            #načítajú sa informácie o tomto protokole
            icmp_result = get_icmp(hex_num, offset)
            data['icmp_type'] = icmp_result[0]
            data['icmp_id'] = icmp_result[1]
            data['icmp_seq'] = icmp_result[2]

        #kontrola, či bol na vstupe zadaný protokol
        if(in_protocol != "all"):
            #odstránenie položiek packets, ipv4_senders, max_send_packets_by z dát
            if in_protocol != "CDP":
                big_data.pop('packets', None)
                big_data.pop('ipv4_senders', None)
                big_data.pop('max_send_packets_by', None)
                big_data['filter_name'] = in_protocol.upper()           #nastavenie nácvu filtra
                exists = False

            #ak bol zadaný niektorý z protokolov nad TCP
            if (in_protocol == app_protocol and data['protocol'] == "TCP"):
                new_com = TcpFilter(data['src_ip'], data['dst_ip'], data['src_port'], data['dst_port'])     #vytvorenie novej TCP komunikácie
                data = clear_data(data)                 #očistenie dát od prázdnych reťazcov
                #ak je pole komunikácii prázdne, vloží sa tam vytvorená komunikácia
                if len(fil) == 0:
                    new_com.add_frame(data)
                    new_com.add_flag(get_flags(hex_num, offset))
                    fil.append(new_com)
                #inak, sa skontrolujú všetky komunikácie v poli a ak sa s niektorou zhodujú, tento rámec sa vloží do tejto komunikácie
                else:
                    for com in fil:
                        if com == new_com:
                            exists = True
                            com.add_frame(data)
                            com.add_flag(get_flags(hex_num, offset))
                            break
                    if not exists:
                        new_com.add_frame(data)
                        new_com.add_flag(get_flags(hex_num, offset))
                        fil.append(new_com)
            
            #ak bol na vstupe zadaný protokol tftp
            elif(in_protocol.lower() == "tftp"):
                #ak je aplikačný protokol zhodný so zadaným protokolom = prvý rámec na porte 69
                if(app_protocol == in_protocol):
                    new_com = TftpFilter(data['src_ip'], data['dst_ip'], data['src_port'], data['dst_port'])    #vytvorí sa nová TFTP komunikácia
                    data = clear_data(data)                 #očistenie dát od prázdnych reťazcov
                    new_com.add_frame(data)
                    new_com.add_opcode(get_opcode(hex_num, offset))
                    fil.append(new_com)                     #komunikácia sa vloží do poľa komunikácii
                
                elif 'protocol' in data:
                    #ak je protokol UDP
                    if data['protocol'] == "UDP":
                        new_com = TftpFilter(data['src_ip'], data['dst_ip'], data['src_port'], data['dst_port'])        #vytvorí sa nová TFTP komunikácia
                        #skontrolujú sa všetky komunikácie v poli a ak sa s niektorou zhodujú, tento rámec sa vloží do tejto komunikácie
                        for com in fil:
                            if com == new_com:
                                exists = True
                                break
                        #ak sa našla zhoda, vloží sa do tejto komunikácie
                        if exists:
                            data = clear_data(data)         #očistenie dát od prázdnych reťazcov
                            com.add_frame(data)
                            com.add_opcode(get_opcode(hex_num, offset))
                        #inak, sa do poľa vloží nová komunikácia
                        else:
                            data = clear_data(data)         #očistenie dát od prázdnych reťazcov
                            new_com.add_frame(data)
                            new_com.add_opcode(get_opcode(hex_num, offset))

            #ak bol na vstupe zadaný protokol icmp
            elif(in_protocol.lower() == "icmp"):
                #ak je protokol rámca zhodný so zadaným vstupným protokolom
                if('protocol' in data and data['protocol'] == in_protocol):
                    #kontrola, či sú dáta fragmentované
                    if data['len_frame_pcap'] > 1500 or fragmented:
                        fragmented = True                   #premenná sa nastaví na True
                        if(has_more_fragments(hex_num)):        #ak daný rámec má viac fragmentov
                            data['flags_mf'] = True
                            frag_icmp = get_fragmented_icmp(hex_num, offset)
                            #odstránia sa položky icmp_type, icmp_id a icmp_seq
                            data.pop('icmp_type', None)
                            data.pop('icmp_id', None)
                            data.pop('icmp_seq', None)
                        else:                                   #inak
                            data['flags_mf'] = False
                            fragmented = False
                        #získanie ďalších informácii pre fragmentované pakety
                        data['frag_offset'] = get_frag_offset(hex_num)
                        data['id'] = get_frag_id(hex_num)
                        #vytvorí sa nový fragment, v ktorom sú uložené rámce, ktoré spolu tvoria jednu informáciu
                        new_com = Fragment(data['src_ip'], data['dst_ip'], data['id'], frag_icmp[0], frag_icmp[1], frag_icmp[2])

                        #ak sa už takýto fragment vyskytuje, aktuálny rámec sa k nemu pridá
                        if fragments.has_fragment(new_com):
                            x = fragments.find_fragment(new_com)
                            f = fragments.fragments[x]
                            data['icmp_type'] = f.icmp_type
                            data['icmp_id'] = f.icmp_id
                            data['icmp_seq'] = f.icmp_seq
                            data = clear_data(data)             #očistenie dát od prázdnych reťazcov
                            f.add_frame(data)
                        elif fragments.is_same(new_com) or len(fragments.fragments) == 0:
                            data = clear_data(data)             #očistenie dát od prázdnych reťazcov
                            new_com.add_frame(data)
                            fragments.add_fragment(new_com)
                        else:
                            fil.append(fragments)
                            fragments = FragmentedICMP()        #vytvorí sa nová fragmentovaná komunikácia
                    
                    #v prípade, že paket nie je fragmentovaný 
                    elif('icmp_id' in data):            #ak icmp_id sa nachádza v dátach
                        new_com = ICMP(data['src_ip'], data['dst_ip'], data['icmp_id'])     #vytvorenie novej ICMP komunikácie
                    else:                               #inak
                        new_com = ICMP(data['src_ip'], data['dst_ip'])                      #vytvorenie novej ICMP komunikácie

                    #ak nová komunikácia nie je fragmentovaná
                    if not isinstance(new_com, Fragment):
                        data = clear_data(data)                 #očistenie dát od prázdnych reťazcov
                        #nová komunikácia sa vloží do poľa komunikácii, ak je toto pole prázdne
                        if len(fil) == 0:
                            new_com.add_frame(data)
                            fil.append(new_com)
                        #inak sa skontroluje každá komunikácia v poli a ak sa nájde zhoda, rámec sa vloží do tejto komunikácie
                        else:
                            for com in fil:
                                if isinstance(com, FragmentedICMP):
                                    break
                                if com == new_com:
                                    exists = True
                                    break
                                elif com.is_same(data):
                                    exists = True
                            if exists:
                                com.add_frame(data)
                            else:
                                new_com.add_frame(data)
                                fil.append(new_com)

            #ak bol na vstupe zadaný protokol arp
            elif in_protocol == "ARP":
                exists = False
                #ak ether_type je zhodný so zadaným vstupným protokolom
                if(data['ether_type'] == in_protocol):
                    new_com = ArpFilter(data['src_ip'], data['dst_ip'])     #vytvorí sa nová komunikácia pre ARP

                    #for-cylus prechádza každú komunikáciu v poli komunikácii
                    for com in fil:
                        #ak sa už takáto komunikácia v poli nachádza, tento rámec sa vloží do komunikácie
                        if com.is_same(new_com, data['arp_opcode']):
                            exists = True
                            data = clear_data(data)                         #očistenie dát od prázdnych reťazcov
                            #podľa arp_opcode sa rámec vloží do príslušného poľa
                            if data['arp_opcode'] == "REQUEST":
                                com.add_request(data)
                            elif data['arp_opcode'] == "REPLY":
                                com.add_reply(data)
                            break
                    #ak neexistuje taká komunikácia, rámec sa vloží do príslušného poľa a celá komunikácia sa vloží do poľa komunikácii
                    if not exists:
                        data = clear_data(data)                             #očistenie dát od prázdnych reťazcov
                        if data['arp_opcode'] == "REQUEST":
                            new_com.add_request(data)
                        elif data['arp_opcode'] == "REPLY":
                            new_com.add_reply(data)
                        fil.append(new_com)

            elif in_protocol == "CDP":
                if data['frame_type'] == "IEEE 802.3 LLC & SNAP" and data['pid'] == in_protocol:
                    data = clear_data(data)
                    cdp_filter.append(data)
                    big_data.pop('filter_name', None)
                    big_data.pop('complete_comms', None)
                    big_data.pop('partial_comms', None)
                    big_data.pop('ipv4_senders', None)
                    big_data.pop('max_send_packets_by', None)
                    
        #na vstupe nebol zadaný protokol
        else:
            data.pop("icmp_id", None)
            data.pop("icmp_seq", None)
            data = clear_data(data)
            big_data["packets"].append(data)        #spojenie paketov s hlavičkou
            #odstránia sa prvky potrebné pre filtrovanie
            big_data.pop('filter_name', None)
            big_data.pop('complete_comms', None)
            big_data.pop('partial_comms', None)

    #ak je pole komunikácii prázdne, vloží sa tam naplnená inštancia triedy pre fragmentovanú komunikáciu
    if len(fragments.fragments) != 0:
        fil.append(fragments)

    #vypisovanie kompletných a nekompletných komunikácii
    complete_counter = 0
    incomplete_counter = 0
    inc_written = False                 #premenná pre kontrolu, či už bola vypísaná nekompletná komunikácia pre TCP
    for i, com in enumerate(fil):
        arp_req = []
        arp_reply = []
        #ak je protokol ARP
        if isinstance(com, ArpFilter):
            complete, arp_req, arp_reply = com.check_complete_com()
            complete_data = {
                'number_comm': 0,
                'packets': []
            }
            partial_data = {
                'number_comm': 0,
                'packets': []
                }
            #kompletné komunikácie
            if len(complete) > 0:
                complete_counter += 1
                complete_data['number_comm'] = complete_counter
                for complete_com in complete:
                    complete_data['packets'].append(complete_com)
            
            #nekompletné komunikácie pre ARP
            if len(arp_req) > 0:
                incomplete_counter += 1
                partial_data['number_comm'] = incomplete_counter
                for req in arp_req:
                    partial_data['packets'].append(req)
            
            if len(arp_reply) > 0:
                incomplete_counter += 1
                partial_data['number_comm'] = incomplete_counter
                for rep in arp_reply:
                    partial_data['packets'].append(rep)
            
            #ak nie sú komunikácie prázdne, vložia sa do slovníka so všetkými údajmi
            if(len(complete) > 0):
                big_data['complete_comms'].append(complete_data)
            if(len(arp_req) > 0 or len(arp_reply) > 0):
                big_data['partial_comms'].append(partial_data)
        
        else:
            #ak bol zadaný protokol nad TCP
            if isinstance(com, TcpFilter):
                new_tcp = com.check_more_comms()        #kontrola, či tam nie je viac otvorení a zatvorení v jednej komunikácii
                if new_tcp is not None:
                    fil.insert(i+1, new_tcp)
            if com.check_complete_com():                #výpis kompletných komunikácii
                complete_counter += 1
                complete_data = {
                    'number_comm': 0,
                    'src_comm': '',
                    'dst_comm': '',
                    'packets': []
                }
                if not isinstance(com, FragmentedICMP):                     #pre fragmentované komunikácie
                    complete_data['number_comm'] = complete_counter
                    complete_data['src_comm'] = com.frames[0]['src_ip']
                    complete_data['dst_comm'] = com.frames[0]['dst_ip']
                    
                    for frame in com.frames:
                        complete_data['packets'].append(frame)

                else:
                    complete_data['number_comm'] = complete_counter
                    complete_data['src_comm'] = com.fragments[0].src
                    complete_data['dst_comm'] = com.fragments[0].dst
                    for frag in com.fragments:
                        for frame in frag.frames:
                            complete_data['packets'].append(frame)

                big_data['complete_comms'].append(complete_data)
            #výpis nekompletných komunikácii
            else:
                incomplete_counter += 1
                incomplete.append(com)
                #ak je protokol nad TCP, vypíše sa iba jedna nekompletná komunikácia
                if in_protocol in TCP.values() and not inc_written:
                    partial_data = {
                    'number_comm': 0,
                    'packets': []
                    }
                    partial_data['number_comm'] = incomplete_counter
                    for frame in com.frames:
                        partial_data['packets'].append(frame)
                    
                    big_data['partial_comms'].append(partial_data)
                    inc_written = True
                #inak sa vypíšu všetky komunikácie
                elif in_protocol not in TCP.values():
                    partial_data = {
                    'number_comm': 0,
                    'packets': []
                    }
                    if not isinstance(com, FragmentedICMP):
                        partial_data['number_comm'] = incomplete_counter
                        for frame in com.frames:
                            frame.pop('icmp_id', None)
                            frame.pop('icmp_seq', None)
                            partial_data['packets'].append(frame)
                    #výpis pre fragmentované komunikácie
                    else:
                        for frag in com.fragments:
                            for frame in frag.frames:
                                partial_data['packets'].append(frame)
                    
                    big_data['partial_comms'].append(partial_data)

    if(in_protocol != "all" and in_protocol != "CDP"):
        if len(big_data['complete_comms']) == 0:
            big_data.pop('complete_comms', None)
        if len(big_data['partial_comms']) == 0:
            big_data.pop('partial_comms', None)

    if(in_protocol == "CDP"):
        for fr in cdp_filter:
            big_data['packets'].append(fr)

        big_data['number_frames'] = len(cdp_filter)

    #nebol zadaný vstupný protokol
    if(in_protocol == 'all'):
        max_packets_sent = 0
        top_senders = []
        #pre všetky IP adresy sa vypíše koľko dát daná IP adresa odoslala
        for ip in ip_addresses:
            ip_data = {}
            packets_sent = ip_addresses[ip]
            if packets_sent > max_packets_sent:
                top_senders = []
                max_packets_sent = packets_sent
                top_senders.append(ip)
            elif packets_sent == max_packets_sent:
                top_senders.append(ip)
            ip_data['node'] = ip
            ip_data['number_of_sent_packets'] = packets_sent
            big_data["ipv4_senders"].append(ip_data)

        big_data['max_send_packets_by'] = top_senders

    yaml.dump(big_data, file)                       #uloženie do .yaml súboru