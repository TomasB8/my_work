#! /bin/bash
#
# Meno: Tomáš Brček
# Kruzok: Streda 15:00
# Datum: 26.11.2023
# Zadanie: zadanie02
#
# Text zadania:
#
# V zadanych textovych suboroch uvedenych ako argumenty najdite najdlhsi riadok
# (riadky) zo vsetkych a vypiste ho (ich). Dlzka riadku je jeho dlzka v znakoch.
# Ak nebude uvedeny ako argument ziadny subor, prehladava sa standardny vstup
# (a jeho meno je -).
#
# Syntax:
# zadanie.sh [-h] [cesta ...]
#
# Vystup ma tvar:
# Output: '<subor>: <cislo riadku v subore> <dlzka riadku> <riadok>'
#
# Priklad vystupu (parametrami boli subory nahodny ine/lorem_ipsum
# v adresari /public/testovaci_adresar/testdir2):
# Output: 'ine/lorem_ipsum: 11 98 eu ipsum. Aliquam viverra vestibulum pretium...
# Output: 'nahodny: 3 98 UtRybYIDDPudgG!YUC?NTpgo,M!vsb.wFrTQtoacxOxnQtDVDzOfnPad...
# Output: 'nahodny: 4 98 UtRybYIDDPudgG!YUC?NTpgo,M!vsb.wFrTQtoacxOxnQtDVDzOfnPad...
#
#
# Program musi osetrovat pocet a spravnost argumentov. Program musi mat help,
# ktory sa vypise pri zadani argumentu -h a ma tvar:
# Meno programu (C) meno autora
#
# Usage: <meno_programu> <arg1> <arg2> ...
#       <arg1>: xxxxxx
#       <arg2>: yyyyy
#
# Parametre uvedene v <> treba nahradit skutocnymi hodnotami.
# Ked ma skript prehladavat adresare, tak vzdy treba prehladat vsetky zadane
# adresare a vsetky ich podadresare do hlbky.
# Pri hladani maxim alebo minim treba vzdy najst maximum (minimum) vo vsetkych
# zadanych adresaroch (suboroch) spolu. Ked viacero suborov (adresarov, ...)
# splna maximum (minimum), treba vypisat vsetky.
#
# Korektny vystup programu musi ist na standardny vystup (stdout).
# Chybovy vystup programu by mal ist na chybovy vystup (stderr).
# Chybovy vystup musi mat tvar (vratane apostrofov):
# Error: 'adresar, subor, ... pri ktorom nastala chyba': popis chyby ...
# Ak program pouziva nejake pomocne vypisy, musia mat tvar:
# Debug: vypis .
#
# Poznamky: (sem vlozte pripadne poznamky k vypracovanemu zadaniu)
#
# Riesenie:

#funkcia na vypis help po zadani argumentu -h
helper () {
	echo "$0 (C) Tomáš Brček"
	echo ""
	echo "Usage: $0 -h cesta ..."
	echo -e "\t-h: Vypisanie help spravy."
	echo -e "\tcesta: Cesta k suboru na spracovanie."
}

#funkcia na hladanie najdlhsieho riadka zo vstupu
find_longest_row () {
	max=$(awk 'BEGIN{max=0} max<length{max=length} END{print max}' subor.txt)
	awk -v x="$max" -v f="-" 'x == length {gsub("\r", ""); printf "Output: '\''%s: %d %d %s'\''\n", f, NR, length, $0 }' subor.txt
}

#funkcia na citanie zo vstupu
read_from_input () {

	rm subor.txt 2> /dev/null		#odstranenie suboru ak existoval

	IFS=$'\n'
	counter=0
	while read -r line; do
		((counter++))
		echo "$line" >> subor.txt	#citanie riadka a ulozenie do suboru
	done
	
	#ak bol zadany aspon jeden znak, bude sa vyhodnocovat najdlhsi riadok, inak sa vypise chyba
	if [ "$counter" -gt 0 ]; then
		find_longest_row

		rm subor.txt
		exit 0
	else
		echo "Error: '-': vstup nebol zadany" >&2
                exit 1
	fi
}

#ak je pocet argumentov 0, program cita zo vstupu
if [ $# == 0 ]; then
	read_from_input
else
	files=()
	for arg in "$@"; do
		if [ "$arg" == "-h" ]; then
                        helper 
                        exit 0
                elif [[ "$arg" == -* ]]; then
                        echo "Error: '-': neznamy prepinac $arg" >&2
                        exit 1
		else
			files+=("$arg")
		fi
	done

	#kontrola spravnosti zadanych argumentov
	output=0
	for file in "${files[@]}";
	do
		#kontrola, ci subor existuje
		if [ -e "$file" ]; then
			#kontrola, ci subor je subor a nie je adresar
			if [ -f "$file" ]; then
				#kontrola, ci ma subor read opravnenia
				if [ -r "$file" ]; then
					#kontrola, ci je subor textovy
                                	if file "$file" | grep -q "text"; then
						continue
					#kontrola, ci subor nie je prazdny
                                	elif file "$file" | grep -q "empty"; then
                                       		echo "Error: '$file': subor je prazdny" >&2
                                       		output=1
                                       		for i in "${!files[@]}"; do
                                               		if [[ ${files[i]} = "$file" ]]; then
                                                       		unset 'files[i]'
                                               		fi
                                       		done

                                	else
                                       		echo "Error: '$file': subor nie je textovy subor" >&2
                                       		output=1
                                       		for i in "${!files[@]}"; do
                                               		if [[ ${files[i]} = "$file" ]]; then
                                                       		unset 'files[i]'
                                               		fi
                                       		done

                                	fi
				else
					echo "Error: '$file': subor nema prava na citanie" >&2
				        output=1	
  					for i in "${!files[@]}"; do
    						if [[ ${files[i]} = "$file" ]]; then
      							unset 'files[i]'
    						fi
  					done
				fi
			else
				echo "Error: '$file': nie je textovy subor" >&2
				output=1
                                for i in "${!files[@]}"; do
                                        if [[ ${files[i]} = "$file" ]]; then
                                                unset 'files[i]'
                                        fi        
                                done
			fi
		else
			echo "Error: '$file': subor neexistuje " >&2
			output=1
                        for i in "${!files[@]}"; do
                                if [[ ${files[i]} = "$file" ]]; then
                                        unset 'files[i]'
                                fi                
                        done
		fi
	done

	if [ ${#files[@]} -eq 0 ]; then	
		exit 1
	fi
					
	#maximalna dlzka riadka zo vsetkych suborov
	max=$(awk 'BEGIN{max=0} max<length{max=length} END{print max}' "${files[@]}")
	
	#inak sa prejdu vsetky subory a ak sa dlzka aktualneho riadku rovna maximalnej dlzke riadku, tak sa vypise
	for file in "${files[@]}";
	do
		awk -v x="$max" -v f="$file" 'x == length {gsub("\r", ""); printf "Output: '\''%s: %d %d %s'\''\n", f, NR, length, $0 }' "$file"

	done
fi
exit $output

