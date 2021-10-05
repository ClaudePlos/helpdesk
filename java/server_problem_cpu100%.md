1. in command line: ps -ef | grep java >>>>> i będziesz miał po nazwie szeryf lub oracle id procesu java glassfisha np: 139533
2. in command line: top -H -p 139533  >>>>> i jest widok na procesy java i ile cpu zużywa, i szukasz tam gdzie jest 100% 
3. in command line: printf "%x \n" 139533 >>> i dostaniesz wartość do logów np: 2210d
4. in command line: jcmd >> powinieneś zobaczyć NetworkServerControl start
5. in command line: jcmd 139533 help >>> lista komend, nie wiem po co to
6. in command line: jcmd 139533 Thread.print > td.txt >>> po wykonaniu wejdź do pliku i znajdź 2210b, powinno pokazać klasę javy i linię kodu tam gdzie jest problem i 100% obciążenie 

END
