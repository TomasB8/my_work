import socket
import threading
import time
import math
import random
import os
import sys
import zlib

FORMAT = "utf-8"        #format na formatovanie dat

#definovanie globalnych premennych
showKA = False
threadRunning = True
connection_lost = False
pause_thread = False
switch = False
end = False
showMenu = True
wantChange = False
inputRunning = True

path = os.getcwd()


###########################################################################################################################
######################################################## SPOLOCNE #########################################################
###########################################################################################################################

#funkcia, ktora resetuje hodnoty globalnych premennych na povodne hodnoty
def reset_global_variables():
    global showKA, threadRunning, connection_lost, pause_thread, switch, showMenu, wantChange, inputRunning
    showKA = False
    threadRunning = True
    connection_lost = False
    pause_thread = False
    switch = False
    showMenu = True
    wantChange = False
    inputRunning = True

#--------------------------------------------------------------------------------------------------------------------------

#funkcia, ktora prepne pouzivatelov
def switch_users(who, server, port, ip):
    if server:
        who.close()
        time.sleep(6)       #oneskorenie z dovodu doposielania posledneho keep-alive paketu
        connect_client(ip, port)
    else:
        who.close()
        connect_server(port)

#--------------------------------------------------------------------------------------------------------------------------

#funkcia na vypocet kontrolnej sumy
def checksum_calculator(data):
    return zlib.crc32(data)

#--------------------------------------------------------------------------------------------------------------------------

#funkcia, ktora sluzi na odosielanie dat od sendera k receiverovi
def send(sender, receiver, type, fragment_number, message, errors=False, text=True):
    if text:
        msg = message.encode(FORMAT)        #ak je to textova sprava, zakoduje sa
    else:
        msg = message                       #ak je subor, tak sa nekoduje
    checksum = checksum_calculator(type.encode(FORMAT) + len(msg).to_bytes(2, byteorder='big') + fragment_number.to_bytes(4, byteorder='big') + msg)
    if errors and random.random() < 0.05:        #s pravdepodobnostou 5% moze dochadzat ku chybam
        checksum += 1
    
    #zakodovanie mojej hlavicky
    udp_header = type.encode(FORMAT) + len(msg).to_bytes(2, byteorder='big') + \
                 fragment_number.to_bytes(4, byteorder='big') + \
                 checksum.to_bytes(4, byteorder='big')
    sender.sendto(udp_header + msg, receiver)           #odoslanie hlavicky spolu s datami

#funkcia, ktora sluzi na prijatie spravy, jej rozdelenie na hlavicku a data a rodelenie a rozkodovanie hlavicky
def receive(data, text=True):
    header = data[:11]
    d = data[11:]

    type_bytes = header[:1]         #typ spravy
    message_length = int.from_bytes(header[1:3], byteorder='big')       #dlzka spravy
    fragment_number = int.from_bytes(header[3:7], byteorder='big')      #cislo fragmentu
    checksum = int.from_bytes(header[7:11], byteorder='big')            #kontrolna suma
    
    type = type_bytes.decode(FORMAT)
    
    if text:
        d = d.decode(FORMAT)
    
    return type, message_length, fragment_number, checksum, d       #vrati tuple so vsetkymi udajmi


###########################################################################################################################
######################################################### SERVER ##########################################################
###########################################################################################################################

def listen_input():
    global inputRunning, showKA, path, switch, end
    #ak je nastavene vypisovanie KA sprav, tak sa vypise
    while True:
        if not inputRunning:
            return
        inp = input().strip()

        if wantChange:
            inputRunning = False
        else:
            if inp == "1":
                print("[WAITING] Waiting for next Keep-Alive packet.")
                switch = True
                break
            elif inp == "2":
                showKA = not showKA
            elif inp == "3":
                #nacitanie cesty, kde ma byt ulozeny subor
                path = input("[CHANGE PATH] Absolute path to directory: ")
                #ak cesta neexistuje, pyta si ju znova, az kym nie je zadana validna cesta
                while not os.path.exists(path):
                    print("[ERROR] Path does not exist. Try again.")
                    path = input("[CHANGE PATH] Absolute path to directory: ")
                    print("--------------------------------------------------------------------------------")
            elif inp == "4":
                print(f"[PATH] Path: {path}")
                print("--------------------------------------------------------------------------------")
            elif inp == "5":
                end = True
                break
            else:
                print("--------------------------------------------------------------------------------")
                print("1 - Switch users")
                print("2 - (Don't) show Keep-Alive messages")
                print("3 - Change path to current directory")
                print("4 - Show path to current directory")
                print("5 - End")
                print("--------------------------------------------------------------------------------")

#--------------------------------------------------------------------------------------------------------------------------

#funkcia, ktora sluzi na prijimanie spravy na strane servera    
def receive_message(data, address, server):
    global connection_lost
    result = []             #pole pre vysledok
    sent_packet_size = 0        #celkova prijata velkost spravy
    expected_num = 1            #ocakavane nasledujuce cislo fragmentu
    while True:
        try:
            server.settimeout(20)
            data = server.recv(1500)
            data = receive(data)
            #prijatie koncovej spravy o prijati celej odosielanej spravy
            if data[0] == "5":
                seen_numbers = set()
                result = [tup for tup in result if tup[0] not in seen_numbers and (seen_numbers.add(tup[0]) or True)]   #odstranenie duplikatov
                result = sorted(result, key=lambda x: x[0])         #zoradenie podla cisla fragmentov
                result = ''.join([tpl[1] for tpl in result])        #spojenie spravy do jedneho stringu
                print(f"[RECEIVE END] Receiving message ended.")
                print(f"[MESSAGE RECEIVED] Message: {result}")
                print("--------------------------------------------------------------------------------")
                send(server, address, "1", 0, "")       #ukoncenie
                break

            correct_checksum = data[3]      #kontrolna suma z hlavicky
            current_checksum = checksum_calculator(data[0].encode(FORMAT) + data[1].to_bytes(2, byteorder='big') + data[2].to_bytes(4, byteorder='big') + data[4].encode(FORMAT))      #vypocet kontrolnej sumy znova
            #ak sa rovnaju posiela sa ACK, inak NACK
            if correct_checksum == current_checksum:
                print(f"[RECEIVED] Packet number {data[2]} was received. Size: {data[1]}B")
                sent_packet_size += data[1]     #pripocitanie velkosti
                result.append((data[2], data[4]))

                #ak prisiel paket, ktory ocakaval, odosle iba ACK
                if data[2] == expected_num:
                    expected_num += 1
                    send(server, address, "1", data[2], "")
                #ak prisiel paket s cislom fragmentu nizsim ako ocakaval, odosle sa iba ACK
                elif data[2] < expected_num:
                    send(server, address, "1", data[2], "")
                #inak odosiela ACK aj s NACK
                else:
                    message = f"{data[2]};"
                    for i in range(expected_num, data[2]):
                        message += f"{i},"
                    send(server, address, "1", 0, message)
                    expected_num = data[2]+1
            #nerovnajuce sa kontrolne sumy a odoslanie NACK
            else:
                print(f"[ERROR] There was an error receiving packet {data[2]}.")
                #ak prisiel paket, ktory ocakaval, odosle iba jedno NACK
                if data[2] == expected_num:
                    expected_num += 1
                    send(server, address, "2", data[2], "")
                #ak prisiel paket s cislom fragmentu nizsim ako ocakaval, odosle sa iba jedno NACK
                elif data[2] < expected_num:
                    send(server, address, "2", data[2], "")
                #inak odosiela viacero NACK na vsetky neprijate spravy
                else:
                    message = f"{data[2]},"
                    for i in range(expected_num, data[2]):
                        message += f"{i},"
                    send(server, address, "2", 0, message)
                    expected_num = data[2]+1
        except (socket.timeout, socket.gaierror):
            print(f"[ERROR] Receiving message failed, try again.")
            connection_lost = True
            return
        
#--------------------------------------------------------------------------------------------------------------------------
        
#funkcia, ktora sluzi na prijimanie spravy na strane servera 
def receive_file(data, address, server, filename):
    global connection_lost, path
    sent_packet_size = 0        #celkova prijata velkost spravy
    expected_num = 1        #ocakavane nasledujuce cislo fragmentu
    result = []             #pole pre vysledok
    #pridanie /, ak sa nenachadza na konci cesty
    if "\\" in path and path[-1] != "\\":
        path += "\\"
    elif "/" in path and path[-1] != "/":
        path += "/"
    while True:
        try:
            server.settimeout(18)
            data = server.recv(1500)
            data = receive(data, False)
            #prijatie koncovej spravy o prijati celej odosielanej spravy
            if data[0] == "5":
                seen_numbers = set()
                result = [tup for tup in result if tup[0] not in seen_numbers and (seen_numbers.add(tup[0]) or True)]       #odstranenie duplikatov
                result = sorted(result, key=lambda x: x[0])     #zoradenie podla cisla fragmentov
                #vlozenie dat do suboru
                with open(f"{path}{filename}", "wb") as f:
                    for r in result:
                        f.write(r[1])
                print(f"[RECEIVE END] Receiving file ended.")
                print(f"[FILE PATH] Absolute path: {path}{filename}")
                print(f"[FILE SIZE] File size: {sent_packet_size}B")
                print("--------------------------------------------------------------------------------")
                send(server, address, "1", 0, "")       #ukoncenie
                break

            correct_checksum = data[3]          #kontrolna suma z hlavicky
            current_checksum = checksum_calculator(data[0].encode(FORMAT) + data[1].to_bytes(2, byteorder='big') + data[2].to_bytes(4, byteorder='big') + data[4])     #vypocet kontrolnej sumy znova
            #ak sa rovnaju posiela sa ACK, inak NACK
            if correct_checksum == current_checksum:
                print(f"[RECEIVED] Packet number {data[2]} was received. Size: {data[1]}B")
                sent_packet_size += data[1]         #pripocitanie velkosti
                result.append((data[2], data[4]))

                #ak prisiel paket, ktory ocakaval, odosle iba ACK
                if data[2] == expected_num:
                    expected_num += 1
                    send(server, address, "1", data[2], "")
                #ak prisiel paket s cislom fragmentu nizsim ako ocakaval, odosle sa iba ACK
                elif data[2] < expected_num:
                    send(server, address, "1", data[2], "")
                #inak odosiela ACK aj s NACK
                else:
                    message = f"{data[2]};"
                    for i in range(expected_num, data[2]):
                        message += f"{i},"
                    send(server, address, "1", 0, message)
                    expected_num = data[2]+1
            #nerovnajuce sa kontrolne sumy a odoslanie NACK
            else:
                print(f"[ERROR] There was an error sending packet {data[2]}.")
                #ak prisiel paket, ktory ocakaval, odosle iba jedno NACK
                if data[2] == expected_num:
                    expected_num += 1
                    send(server, address, "2", data[2], "")
                #ak prisiel paket s cislom fragmentu nizsim ako ocakaval, odosle sa iba jedno NACK
                elif data[2] < expected_num:
                    send(server, address, "2", data[2], "")
                #inak odosiela viacero NACK na vsetky neprijate spravy
                else:
                    message = f"{data[2]},"
                    for i in range(expected_num, data[2]):
                        message += f"{i},"
                    send(server, address, "2", 0, message)
                    expected_num = data[2]+1
        except (socket.timeout, socket.gaierror):
            print(f"[ERROR] Receiving file failed, try again.")
            connection_lost = True
            return
        
#--------------------------------------------------------------------------------------------------------------------------

#funkcia, ktora spracovava server
def handle_server(server, client):
    global showKA, switch, wantChange, inputRunning
    reset_global_variables()        #resetovanie globalnych premennych
    input_thread = threading.Thread(target=listen_input)
    input_thread.start()
    while True:
        try:
            if connection_lost:
                raise socket.timeout
            #prijatie dat od servera
            server.settimeout(60)
            data, address = server.recvfrom(1500)
            data = receive(data)
            info = data[0]      #typ spravy
            #prisla inicializacna sprava na odosielanie dat
            if switch and info == "1":
                inputRunning = False
                print(f"[SWITCH] Switching users.")
                print("--------------------------------------------------------------------------------")
                switch_users(server, True, address[1], address[0])
                break
            elif end and info == "1":
                inputRunning = False
                print(f"[END] Ending connection with {address}.")
                print("--------------------------------------------------------------------------------")
                server.close()
                sys.exit(1)
            elif info == "3":
                if data[4][0] == "t":       #bude sa odosielat sprava
                    packets_num = int(data[4][1:])      #pocet paketov
                    print(f"[RECEIVING] Receiving {packets_num} packets...")
                    send(server, address, "1", 0, "")           #odoslanie ACK
                    receive_message(data, address, server)      #server zacne prijimat data
                elif data[4][0] == "f":     #bude sa odosielat subor
                    i = data[4].rfind(";")      #v datach sa najde bodka
                    packets_num = data[4][1:i]      #pocet fragmentov
                    print(f"[RECEIVING] Receiving {packets_num} packets...")
                    filename = data[4][i+1:]            #nacitanie pripony suboru
                    
                    send(server, address, "1", 0, "")       #odoslanie ACK
                    receive_file(data, address, server, filename)     #server zacina prijimat data
            #prisla keep-alive sprava
            elif info == "6":
                #ak je nastavene vypisovanie KA sprav, tak sa vypise
                if showKA:
                    print(f"[KEEP-ALIVE] Keep-alive packet.")
                if not switch and not end:
                    send(server, address, "1", 0, "")
                elif switch:
                    send(server, client, "7", 0, "")
                else:
                    send(server, client, "8", 0, "")

            #prisla poziadavka na prepnutie roli
            elif info == "7":
                print("[SWITCH] Press ENTER...")
                wantChange = True
                while inputRunning:
                    continue
                print(f"[SWITCH] Switching users.")
                print("--------------------------------------------------------------------------------")
                send(server, address, "1", 0, "")       #odoslanie ACK
                switch_users(server, True, address[1], address[0])      #prepnutie roli
                switch = True
                break
            #ukoncenie spojenia a programu
            elif info == "8":
                print("[SWITCH] Press ENTER...")
                wantChange = True
                while inputRunning:
                    continue
                print(f"[END] Ending connection with {address}.")
                print("--------------------------------------------------------------------------------")
                send(server, address, "1", 0, "")
                inputRunning = False
                server.close()
                sys.exit(1)

        except (socket.timeout, socket.gaierror):
            print(f"[END] Connection with {address} lost.")
            print("--------------------------------------------------------------------------------")
            server.settimeout(None)
            break

#--------------------------------------------------------------------------------------------------------------------------

#funkcia na pripojenie servera
def connect_server(port=None):
    reset_global_variables()        #resetovanie globalnych premennych
    server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    while port is None:
        try:
            port = int(input("Port: "))     #nacitanie portu zo vstupu
        except ValueError:
            print("[ERROR] Wrong port number. Try again.")
            continue
    server_ip = socket.gethostbyname(socket.gethostname())
    print(server_ip)
    server_addr = (server_ip, port)
    server.bind(server_addr)        #pripojenie servera
    #cakanie na spravu od klienta
    while True:
        data, address = server.recvfrom(1500)
        send(server, address, "1", 0, "")   #odoslanie ACK
        print("[CONNECTION] Connection from address:", address)
        print("--------------------------------------------------------------------------------")
        handle_server(server, address)       #spracovavanie servera
        if switch:
            break

###########################################################################################################################
######################################################### KLIENT ##########################################################
###########################################################################################################################

#funkcia, ktora sluzi na odoslanie keep-alive packetu
def send_packet(client, server_address):
    global connection_lost, showMenu, threadRunning, wantChange, pause_thread, end
    #ak nebolo posielanie sprav pozastavene, budu sa posielat
    if not pause_thread:
        try:
            #odoslanie packetu na server
            send(client, server_address, "6", 0, "")
            client.settimeout(2)
            #nacitanie prijatych dat
            data = client.recv(1500)
            data = receive(data)
            connection_lost = False
            #ak sme zvolili moznost zobrazovania keep-alive sprav, budu sa vypisovat
            if data[0] == "1" and showKA:
                print("[KEEP-ALIVE] Server is connected.")
            #ak je typ spravy 8, server sa chce vymenit
            if data[0] == "7":
                pause_thread = True         #pozastavenie keep-alive sprav
                wantChange = True
                print(f"\n[SWITCH] Press ENTER...", end="")      #vypytanie si potvrdenia od klienta
            elif data[0] == "8":
                pause_thread = True
                end = True
                print(f"\n[END] Press ENTER...", end="")      #vypytanie si potvrdenia od klienta
        #spojenie bolo prerusene
        except (socket.timeout, socket.gaierror, ConnectionResetError):
            connection_lost = True
            print("[ERROR] Server is unreachable. Waiting for reconnection.")

#funkcia na udrziavanie spojenia
def maintain_connection(client, server_address):
    while True:
        if not threadRunning:
            return
        #posielanie paketu kazdych 5 sekund
        send_packet(client, server_address)
        time.sleep(5)

#--------------------------------------------------------------------------------------------------------------------------

#funkcia, ktora sluzi na odosielanie sprav
def send_message(client, server_addr):
    msg = input("Message: ")        #nacitanie spravy
    n = 4                           #velkost okna
    send_again = []
    packets = []
    #nacitanie maximalnej velkosti fragmentu (0,1461>
    fragment_size = -1
    while not (0 < fragment_size <= 1461):
        fragment_size = int(input("Fragment size (1B - 1461B): "))
    errors = None
    while errors == None:
        c = input("Errors [Y/n]: ")
        if c.lower() == "y":
            errors = True
        elif c.lower() == "n":
            errors = False
        elif c == "":
            errors = True
    
    #vypocet poctu paketov, ktore treba odoslat
    packets_num = math.ceil(len(msg) / fragment_size)
    print(f"[SENDING] Sending {packets_num} packets...")

    try:
        send(client, server_addr, "3", 0, "t"+str(packets_num))     #odoslanie inicializacnej spravy na odosielanie sprav
        client.settimeout(50)
        #prijatie dat zo servera
        data = client.recv(1500)
        data = receive(data)
        sender = [i+1 for i in range(packets_num)]      #pole cisel paketov, ktore treba odoslat
        if data[0] == "1":
            #ak je velkost okna vacsia ako pocet paketov, tak sa velkost okna rovna poctu paketov
            if n > packets_num:
                n = packets_num

            #odoslanie prvych n paketov
            for i in range(n):
                packets.append([sender[0], 2*n, msg[:fragment_size]])
                send(client, server_addr, "4", sender.pop(0), msg[:fragment_size], errors=errors)
                client.settimeout(5)
                msg = msg[fragment_size:]       #odstranenie precitanej casti spravy

            while True:
                try:
                    data = receive(client.recv(1500))           #prijatie ack / nack
                    #prijatie typu spravy 1
                    if data[0] == "1":
                        received_ack = None
                        received_nack = None
                        #zistenie, ci prislo iba ack alebo ack aj s nack
                        if ";" in data[4]:
                            ack, nacks = data[4].split(";")     #rozdelenie jednotlivych poli
                            nacks = nacks[:-1].split(",")       #oddelenie vsetkych nack
                            received_ack = int(ack)             #prijate ACK
                            received_nack = [int(seq) for seq in nacks]     #vsetky prijate NACK
                        else:
                            received_ack = data[2]

                        #najdenie paketu, na ktory prislo ACK a jeho odstranenie z packets
                        msg_size = 0
                        for i, tup in enumerate(packets):
                            if tup[0] == received_ack:
                                msg_size = len(tup[2])
                                packets.pop(i)
                                break
                        print(f"[RESPONSE] Fragment {received_ack} was received. Size: {msg_size}B")        #sprava o prijati
                        #ak boli prijate aj nejake NACK, vypise sa pre ne chybova hlaska a vlozia sa do pola send_again
                        if received_nack != None:
                            for nack in received_nack:
                                print(f"[ERROR] Sending fragment {nack} failed, sending again.")
                                send_again.append(nack)
                    #prislo nack
                    elif data[0] == "2":
                        #zistovanie, ci prislo viac NACK v jednom
                        if "," in data[4]:
                            nacks = data[4][:-1].split(",")
                            nacks = [int(seq) for seq in nacks]     #nacitanie vsetkych NACK
                            for nack in nacks:      #vypis chybovych hlasok a vlozenie do pola send_again
                                print(f"[ERROR] Sending fragment {nack} failed, sending again.")
                                send_again.append(nack)
                        else:
                            #v pripade, ze prislo iba jedno NACK
                            received_nack = data[2]
                            print(f"[ERROR] Sending fragment {received_nack} failed, sending again.")
                            send_again.append(received_nack)

                    #pre vsetky pakety v poli packets sa od pocitadla odcita 1
                    for p in packets:
                        p[1] -= 1
                        #ak je pocitadlo 0, cas vyprsal a packet sa odosle znova
                        if p[1] == 0:
                            send_again.append(p[0])

                    #znovuodoslanie vsetkych paketov, ktore su ulozene v poli send_again
                    while len(send_again) != 0:
                        old_msg = ""
                        for i, tup in enumerate(packets):
                            if tup[0] == send_again[0]:
                                old_msg = tup[2]
                                packets.pop(i)      #odstranenie z pola packets
                                break
                        packets.append([send_again[0], 2*n, old_msg])       #vlozenie do pola packets znova, aj s resetovanym pocitadlom
                        send(client, server_addr, "4", send_again.pop(0), old_msg)      #odoslanie dat
                        client.settimeout(5)
                    #ak je volne miesto na odoslanie paketu a mame este co poslat, tak sa posle dalsi paket
                    if len(sender) != 0:
                        while len(packets) != n and len(sender) != 0:
                            packets.append([sender[0], 2*n, msg[:fragment_size]])       #vlozenie do pola packets
                            send(client, server_addr, "4", sender.pop(0), msg[:fragment_size], errors=errors)
                            client.settimeout(5)
                            msg = msg[fragment_size:]       #nacitanie a posunutie spravy

                    #ak boli vsetky polia vyprazdnene, posiela sa koncova sprava
                    if len(sender) == 0 and len(send_again) == 0 and len(packets) == 0:
                        send(client, server_addr, "5", 0, "")
                        client.settimeout(5)
                        resp = client.recv(1500)
                        resp = receive(resp)
                        #po prijati ACK sa ukonci odosielanie
                        if resp[0] == "1":
                            print(f"[END] Sending fragments ended.")
                            break
                except (socket.timeout, socket.gaierror):
                    if counter < 3:
                        send(client, server_addr, "4", packets[0][0], packets[0][2])
                        print(f"[SENDING] Sending fragment {packets[0][0]}, again.")
                        counter += 1
                        continue
                    else:
                        print(f"[ERROR] Sending message failed, try again.")
                        break

    except (socket.timeout, socket.gaierror):
        print(f"[ERROR] Sending message failed, try again.")
        return

#--------------------------------------------------------------------------------------------------------------------------

#funkcia, ktora sluzi na odosielanie suborov
def send_file(client, server_addr):
    n = 20          #velkost okna
    counter = 0
    packets = []
    send_again = []
    file = None
    #nacitanie suboru z aktualneho adresara
    while True:
        try:
            if file is not None:
                break
            filename = input("Filename: ")
            file = open(filename, "rb")
        except FileNotFoundError:
            print("[ERROR] File not found, try again.")
            continue

    #nacitanie maximalnej velkosti fragmentu (0,1461>
    fragment_size = -1
    while not (0 < fragment_size <= 1461):
        fragment_size = int(input("Fragment size (1B - 1461): "))
    
    errors = None
    while errors == None:
        c = input("Errors [Y/n]: ")
        if c.lower() == "y":
            errors = True
        elif c.lower() == "n":
            errors = False
        elif c == "":
            errors = True
    
    path = os.path.abspath(filename)        #cesta k zadanemu suboru
    file_size = os.path.getsize(path)       #velkost suboru
    #vypocet poctu paketov, ktore treba odoslat
    packets_num = math.ceil(file_size / fragment_size)
    sender = [i+1 for i in range(packets_num)]      #pole cisel paketov, ktore treba odoslat
    print(f"[FILE PATH] Path to file: {path}")
    print(f"[FILE SIZE] File size: {file_size}B")
    print(f"[SENDING] Sending {packets_num} packets...")
    filename = os.path.basename(path)      #pripona suboru
    try:
        send(client, server_addr, "3", 0, "f"+str(packets_num)+";"+filename)    #odoslanie inicializacnej spravy na odosielanie suboru
        client.settimeout(50)
        #prijatie dat zo servera
        data = client.recv(1500)
        data = receive(data)
        if data[0] == "1":

            #ak je velkost okna vacsia ako pocet paketov, tak sa velkost okna rovna poctu paketov
            if n > packets_num:
                n = packets_num

            #odoslanie prvych n paketov
            for i in range(n):
                message = file.read(fragment_size)      #precitanie dat zo subora
                packets.append([sender[0], 2*n, message])
                seq_number = sender.pop(0)
                send(client, server_addr, "4", seq_number, message, errors=errors, text=False)
                client.settimeout(5)

            while True:
                try:
                    data = receive(client.recv(1500))       #prijatie dat zo servera
                    #prijatie typu spravy 1
                    if data[0] == "1":
                        received_ack = None
                        received_nack = None
                        #zistenie, ci prislo iba ack alebo ack aj s nack
                        if ";" in data[4]:
                            ack, nacks = data[4].split(";")     #rozdelenie jednotlivych poli
                            nacks = nacks[:-1].split(",")       #oddelenie vsetkych nack
                            received_ack = int(ack)             #prijate ACK
                            received_nack = [int(seq) for seq in nacks]     #vsetky prijate NACK
                        else:
                            received_ack = data[2]

                        #najdenie paketu, na ktory prislo ACK a jeho odstranenie z packets
                        msg_size = 0
                        for i, tup in enumerate(packets):
                            if tup[0] == received_ack:
                                msg_size = len(tup[2])
                                packets.pop(i)
                                break
                        print(f"[RESPONSE] Fragment {received_ack} was received. Size: {msg_size}B")
                        #ak boli prijate aj nejake NACK, vypise sa pre ne chybova hlaska a vlozia sa do pola send_again
                        if received_nack != None:
                            for nack in received_nack:
                                print(f"[ERROR] Sending fragment {nack} failed, sending again.")
                                send_again.append(nack)
                    #prislo NACK
                    elif data[0] == "2":
                        #zistovanie, ci prislo viac NACK v jednom
                        if "," in data[4]:
                            nacks = data[4][:-1].split(",")
                            nacks = [int(seq) for seq in nacks]     #nacitanie vsetkych NACK
                            for nack in nacks:          #vypis chybovych hlasok a vlozenie do pola send_again
                                print(f"[ERROR] Sending fragment {nack} failed, sending again.")
                                send_again.append(nack)
                        else:
                            #v pripade, ze prislo iba jedno NACK
                            received_nack = data[2]
                            print(f"[ERROR] Sending fragment {received_nack} failed, sending again.")
                            send_again.append(received_nack)

                    #pre vsetky pakety v poli packets sa od pocitadla odcita 1
                    for p in packets:
                        p[1] -= 1
                        #ak je pocitadlo 0, cas vyprsal a packet sa odosle znova
                        if p[1] == 0:
                            send_again.append(p[0])

                    #znovuodoslanie vsetkych paketov, ktore su ulozene v poli send_again
                    while len(send_again) != 0:
                        for i, tup in enumerate(packets):
                            if tup[0] == send_again[0]:
                                old_msg = tup[2]
                                packets.pop(i)      #odstranenie z pola packets
                                break
                        packets.append([send_again[0], 2*n, old_msg])       #vlozenie do pola packets znova, aj s resetovanym pocitadlom
                        send(client, server_addr, "4", send_again.pop(0), old_msg, errors=errors, text=False)     #odoslanie dat
                        client.settimeout(5)

                    #ak je volne miesto na odoslanie paketu a mame este co poslat, tak sa posle dalsi paket
                    if len(sender) != 0:
                        while len(packets) != n:
                            message = file.read(fragment_size)
                            packets.append([sender[0], 2*n, message])       #vlozenie do pola packets
                            send(client, server_addr, "4", sender.pop(0), message, errors=errors, text=False)
                            client.settimeout(5)

                    #ak boli vsetky polia vyprazdnene, posiela sa koncova sprava
                    if len(sender) == 0 and len(send_again) == 0 and len(packets) == 0:
                        send(client, server_addr, "5", 0, "")
                        client.settimeout(5)
                        resp = client.recv(1500)
                        resp = receive(resp)
                        #po prijati ACK sa ukonci odosielanie
                        if resp[0] == "1":
                            print(f"[END] Sending fragments ended.")
                            break
                except (socket.timeout, socket.gaierror):
                    if counter < 3:
                        send(client, server_addr, "4", packets[0][0], packets[0][2], text=False)
                        print(f"[SENDING] Sending fragment {packets[0][0]}, again.")
                        counter += 1
                        continue
                    else:
                        print(f"[ERROR] Sending file failed, try again.")
                        break
    except (socket.timeout, socket.gaierror):
        print(f"[ERROR] Sending file failed, try again.")
        return

    file.close()

#--------------------------------------------------------------------------------------------------------------------------

#funkcia, ktora sluzi na spracovanie klienta
def handle_client(client, server_addr):
    global showKA, threadRunning, pause_thread, showMenu, wantChange
    reset_global_variables()        #resetovanie globalnych premennych
    #spustenie vlakna na odosielanie KA sprav
    connection_thread = threading.Thread(target=maintain_connection, args=(client, server_addr))
    connection_thread.start()
    while True:
        #po strateni spojenia, caka v cykle na nadviazanie spojenia
        while connection_lost:
            continue
        try:
            #vypisovanie menu
            if showMenu or wantChange:
                if not wantChange:
                    print("--------------------------------------------------------------------------------")
                    print("1 - Send a message")
                    print("2 - Send a file")
                    print("3 - Show Keep-Alive messages")
                    print("4 - Don't show Keep-Alive messages")
                    print("5 - Switch users")
                    print("6 - End")
                    print("--------------------------------------------------------------------------------")
                what = input("Your choice: ")
                print("--------------------------------------------------------------------------------")
                #ak sa server nechce prepnut
                if not wantChange and not end:
                    #budu sa odosielat spravy
                    if what == "1":
                        pause_thread = True
                        send_message(client, server_addr)
                        pause_thread = False
                    #budu sa odosielat subory
                    elif what == "2":
                        pause_thread = True
                        send_file(client, server_addr)
                        pause_thread = False
                    #zobrazovanie KA sprav
                    elif what == "3":
                        showMenu = False
                        showKA = True
                    #nezobrazovanie KA sprav
                    elif what == "4":
                        showMenu = True
                        showKA = False
                    #prepnutie pouzivatelov
                    elif what == "5":
                        send(client, server_addr, "7", 0, "")
                        client.settimeout(50)
                        pause_thread = True
                        data = receive(client.recv(1500))
                        if data[0] == "1":
                            threadRunning = False
                            time.sleep(5.2)     #oneskorenie z dovodu posielania poslednej KA spravy
                            switch_users(client, False, client.getsockname()[1], client.getsockname()[0])
                            break
                    #ukoncenie spojenia
                    elif what == "6":
                        if connection_lost:
                            threadRunning = False
                            client.close()
                            sys.exit(1)
                        threadRunning = False
                        send(client, server_addr, "8", 0, "")
                        client.settimeout(50)
                        data = receive(client.recv(1500))
                        if data[0] == "1":
                            client.close()
                            sys.exit(1)
                    else:
                        continue    #neznamy vstup
                #server sa chce prepnut
                else:
                    if what or what == "":
                        if wantChange:
                            wantChange = False
                            threadRunning = False
                            send(client, server_addr, "1", 0, "")
                            time.sleep(5.2)
                            switch_users(client, False, client.getsockname()[1], client.getsockname()[0])
                            break
                        elif end:
                            threadRunning = False
                            send(client, server_addr, "1", 0, "")
                            print(f"[END] Ending connection with {server_addr}.")
                            client.close()
                            sys.exit(1)
            else:
                #po zadani Enter sa zobrazi menu
                input()
                showMenu = True
        except ValueError:
            continue
        except OSError:
            pass

#--------------------------------------------------------------------------------------------------------------------------

#funkcia na pripojenie klienta
def connect_client(server_ip=None, port=None):
    reset_global_variables()         #resetovanie globalnych premennych
    counter = 0
    client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    while True:
        if server_ip is None and port is None:
            server_ip = input("Server address: ")       #nacitanie ip adresy servera
            port = int(input("Port: "))                 #nacitanie portu
        server_addr = (server_ip, port)
        try:
            send(client, server_addr, "0", 0, "")       #odoslanie spravy

            client.settimeout(5)
            data, address = client.recvfrom(1500)       #prijatie spravy od servera
            data = receive(data)

            if data[0] == "1":
                print("[CONNECTION] Connected to address:", address)
                print("--------------------------------------------------------------------------------")
                handle_client(client, server_addr)      #spracovanie klienta
        except (socket.timeout, socket.gaierror):
            if counter < 3:
                counter += 1
                continue
            else:
                print("[ERROR] Connection failed. Try again.")
                server_ip = None
                port = None
                continue

#--------------------------------------------------------------------------------------------------------------------------

while True:
    try:
        while True:
            print("--------------------------------------------------------------------------------")
            print("1 - Server")
            print("2 - Client")
            print("3 - exit")
            print("--------------------------------------------------------------------------------")
            role = int(input("Choice: "))
            print("--------------------------------------------------------------------------------")
            if role == 1:
                connect_server()
            elif role == 2:
                connect_client()
            elif role == 3:
                break
            else:
                print("[ERROR] Unknown choice, try again.")
    except ValueError:
        print("[ERROR] Unknown choice, try again.")
    except PermissionError:
        print("[ERROR] Permission to this port is denied. Try again.")
    except KeyboardInterrupt:
        print("[ERROR] Keyboard Interrupt.")
        break