Do zmiany:

Egeria:
- Środki trwałe
- Wyciągi bankowe (131 rachunki bankowe, 149 śrdoki w drodze)
- Raporty kasowe (100, 148 środki w dordze kasy )

i.naprzod:
- książka sekretariatu (kod na front backend i w pakiecie oracle)
- pakiet naprzod2.napwf_fin_zobowiazania (761)

- refaktury  
- dekretacja SOD (761-01-11) - grupa, po za? > ma być 761-01-0-11-000000 -- jaki kontrahent? 

- Spółki cywilne (egadm1.nap_sc_pk)
- PKPZ, zaciąganie danych z MAPI (naprzod2.nap_mapi_tools.dodajDekretyDoPK) 314 / 301



- FZP faktury paliwowe (761-02-99 - pozostałe koszty operacyjne) będzie: 761-02-0-99
- FZT fakutury za telefon (761)

- faktury należnościowe (702 - konta przychodowe) > automat backenf 
- pakiet dla wgrywanych z i.naprzod: nap_mkt_procesy.WGRAJ_FAKTURE_DLA_SK >> dekretacja idzie z EGE dla FVCU (Catering)


- PZ i automaty do rozksięgowania 

- PK - rezerwy urlopowe (301 rozliczenie zakupów poza grupą) > jak oznaczyć powiązane, niepowiązane? 
- PK - rezerwy PZtów

# Słownik - Wzorce
## link do słownika 
<pre>
NAP_KG_KONTA_SLOWNIK
+ warunek listy:
and wsl_sl_nazwa = 'NAP_KG_KONTA_083_INWESTYCJE'
</pre>
## kontrahent grupa 
NAP_KONTRAHENT_GRUPA
grupa / poza
<pre>
where grupa = (case when $POZIOM_2='0' then 'POZA' else 'GRUPA' end)
</pre>
+ leasing
<pre>
where grupa = (case when $POZIOM_2='0' and $POZIOM_3!='06' then 'POZA' when $POZIOM_2='0' and $POZIOM_3='06' then 'LEASING' else 'GRUPA' end)
</pre>



DONE: \
Czy jest w grupie kapitałowej?
<pre>
select frm_id, frm_nazwa from eat_firmy where frm_kl_id in (
select ck_kl_kod from CKK_CECHY_KLIENTOW where ck_ce_id = 100603)-- grupa kapitałowa

declare
l_czyGK boolean;
begin
l_czyGK := naprzod.nap_kg_tools.CZY_FRM_JEST_W_GRUPIE_KAP(300202);
if l_czyGK then
dbms_output.PUT_LINE('TRUE');
else 
dbms_output.PUT_LINE('FALSE');
end if;
end;
</pre>

## Synchronizacja kont 200: 
<pre>
begin
eap_globals.USTAW_firme(300322);
eap_globals.USTAW_konsolidacje('N');
end;

declare
new_knt_id number;
begin
for rek in (
select snk_stare, snk_nowe
,(select knt_pelny_numer from kg_konta where knt_id = snk_stare) konto_stare 
,(select knt_pelny_numer from kg_konta where knt_id = snk_nowe) konto_nowe  
 from KG_SYNCHRONIZACJA_KONT, KG_KONTA 
 where knt_id = snk_stare
   and knt_pelny_numer like '200%' and knt_pelny_numer like '200-0-000003'--'___-_-______'
order by 3 
)
loop
--
select knt_id into new_knt_id
 from kg_konta 
where knt_rp_rok = 2022 
  and knt_pelny_numer = substr(rek.konto_stare,0,4)||'0'||substr(rek.konto_stare,5);
update KG_SYNCHRONIZACJA_KONT set snk_nowe = new_knt_id where snk_stare = rek.snk_stare;
end loop;
end;
</pre>

Wzorzec na 301: + slownik: NAP_KG_KONTA_POW_NIEPOW
<img src="./jpg/kg_wzorzec_301_na _2022_v02.png">




## Synvchronizacja 999 -> 000-999
<pre>

begin
eap_globals.USTAW_firme(300322);
eap_globals.USTAW_konsolidacje('N');
end;

declare
new_knt_id number;
begin
for rek in (
select * from (
select snk_stare, snk_nowe
,(select knt_pelny_numer from kg_konta where knt_id = snk_stare) konto_stare 
,(select knt_pelny_numer from kg_konta where knt_id = snk_nowe) konto_nowe  
 from KG_SYNCHRONIZACJA_KONT, KG_KONTA 
 where knt_id = snk_nowe
   and knt_pelny_numer like '999' 
order by 3 
) where konto_stare not like '5%'
)
loop
--
update KG_SYNCHRONIZACJA_KONT set snk_nowe = 10424790 where snk_stare = rek.snk_stare; --- konto 000-999
end loop;
end;
</pre>

## Weyfikacja synchronizacji
<pre>

kg_zo1.sprawdz_mapowania

  SELECT knt_pelny_numer
      --  INTO v_knt_pelny_numer
        FROM KG_SYNCHRONIZACJA_KONT, KG_KONTA
       WHERE snk_stare = knt_id
         AND knt_posiada_nastepny_seg_id IS NOT NULL
         AND knt_typ = 'B'
         AND ROWNUM = 1;

		     SELECT 1
        --INTO v_dummy
        FROM KG_SYNCHRONIZACJA_KONT, KG_KONTA
       WHERE snk_nowe = knt_id
         AND knt_posiada_nastepny_seg_id IS NOT NULL
         AND knt_typ = 'B';


SELECT *
       -- INTO v_knt_pelny_numer
        FROM KG_SYNCHRONIZACJA_KONT, KG_KONTA
       WHERE snk_stare = knt_id
         AND knt_posiada_nastepny_seg_id IS NOT NULL
         AND knt_typ = 'B'
         AND ROWNUM = 1;
		 
		 
		 
		 SELECT KNT_ID, KNT_PELNY_NUMER, KNT_NAZWA
    FROM KGV_KONTA
   WHERE     knt_rp_rok = 2022 - 1
     AND knt_posiada_nastepny_seg_id IS NULL
     AND knt_typ = 'B'
    and not exists (select 1
                    from kg_synchronizacja_kont
                    where  knt_id = snk_stare
                    group by snk_stare
                    having sum(snk_wspolczynnik) = 1
                    )
</pre>

## Update nazewnictwa 5% - Amortyzacja 
<pre>
select * from kg_konta 
 where knt_rp_rok = 2022
   and knt_pelny_numer like '5%-01-a01'
   and knt_nazwa != 'Amortyzacja jednorazowa'
   
update kg_konta set knt_nazwa = 'Amortyzacja jednorazowa'
, knt_nazwa2 = 'Amortyzacja jednorazowa'
, knt_nazwa3 = 'Amortyzacja jednorazowa'
, knt_nazwa4 = 'Amortyzacja jednorazowa'
, knt_nazwa5 = 'Amortyzacja jednorazowa'
, knt_nazwa6 = 'Amortyzacja jednorazowa'
, knt_nazwa7 = 'Amortyzacja jednorazowa'
, knt_nazwa8 = 'Amortyzacja jednorazowa'
, knt_nazwa9 = 'Amortyzacja jednorazowa'
 where knt_rp_rok = 2022
   and knt_pelny_numer like '5%-01-a01' and knt_nazwa != 'Amortyzacja jednorazowa'
   
commit

select * from kg_konta 
 where knt_rp_rok = 2022
   and knt_pelny_numer like '5%-01-a03'
   and knt_nazwa != 'Amortyzacja w czasie OTW'
   
update kg_konta set knt_nazwa = 'Amortyzacja w czasie OTW'
, knt_nazwa2 = 'Amortyzacja w czasie OTW'
, knt_nazwa3 = 'Amortyzacja w czasie OTW'
, knt_nazwa4 = 'Amortyzacja w czasie OTW'
, knt_nazwa5 = 'Amortyzacja w czasie OTW'
, knt_nazwa6 = 'Amortyzacja w czasie OTW'
, knt_nazwa7 = 'Amortyzacja w czasie OTW'
, knt_nazwa8 = 'Amortyzacja w czasie OTW'
, knt_nazwa9 = 'Amortyzacja w czasie OTW'
 where knt_rp_rok = 2022
   and knt_pelny_numer like '5%-01-a03' and knt_nazwa != 'Amortyzacja w czasie OTW' 
   
commit    


select * from kg_konta 
 where knt_rp_rok = 2022
   and knt_pelny_numer like '5%-01-a05'
   and knt_nazwa != 'Amortyzacja NKUP (dot. samochodów)'
   
update kg_konta set knt_nazwa = 'Amortyzacja NKUP (dot. samochodów)'
, knt_nazwa2 = 'Amortyzacja NKUP (dot. samochodów)'
, knt_nazwa3 = 'Amortyzacja NKUP (dot. samochodów)'
, knt_nazwa4 = 'Amortyzacja NKUP (dot. samochodów)'
, knt_nazwa5 = 'Amortyzacja NKUP (dot. samochodów)'
, knt_nazwa6 = 'Amortyzacja NKUP (dot. samochodów)'
, knt_nazwa7 = 'Amortyzacja NKUP (dot. samochodów)'
, knt_nazwa8 = 'Amortyzacja NKUP (dot. samochodów)'
, knt_nazwa9 = 'Amortyzacja NKUP (dot. samochodów)'
 where knt_rp_rok = 2022
   and knt_pelny_numer like '5%-01-a05' and knt_nazwa != 'Amortyzacja NKUP (dot. samochodów)'
</pre>

#### Zmiana nazw i synchro konta 234%
<pre>
begin
eap_globals.USTAW_firme(300322);
eap_globals.USTAW_konsolidacje('N');
end;


-- 01. change name for 03, 04, , 05

select * from kg_konta 
 where knt_rp_rok = 2022
   and knt_pelny_numer like '234%03'
   and knt_nazwa != 'Karty sportowe Fit Profit'
   
update kg_konta set knt_nazwa = 'Karty sportowe Fit Profit'
, knt_nazwa2 = 'Karty sportowe Fit Profit'
, knt_nazwa3 = 'Karty sportowe Fit Profit'
, knt_nazwa4 = 'Karty sportowe Fit Profit'
, knt_nazwa5 = 'Karty sportowe Fit Profit'
, knt_nazwa6 = 'Karty sportowe Fit Profit'
, knt_nazwa7 = 'Karty sportowe Fit Profit'
, knt_nazwa8 = 'Karty sportowe Fit Profit'
, knt_nazwa9 = 'Karty sportowe Fit Profit'
 where knt_rp_rok = 2022
   and knt_pelny_numer like '234%03' and knt_nazwa != 'Karty sportowe Fit Profit'

   commit
</pre>
