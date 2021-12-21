#### 2021.12.21 
#### Porównianie PZ do generowanych WZ: konto 314-11 wykaz dal WN i MA. Jeżeli saldo jest różne to przejdź do B:
<pre>
A: 
select sum(ks_wart) from (
select dok_id, dok_numer_wlasny, pz_wart, ks_wart, pz_wart - ks_wart spr from (
 select dok_id, dok_numer_wlasny, sum(pdok_wartosc) pz_wart
 , (select sum(ks_kwota) from kgt_ksiegowania where ks_dok_id = dok_id) ks_wart
  from kgt_dokumenty, gmt_pozycje_dokumentow
 where pdok_dok_id = dok_id
   and dok_f_wstrzymany = 'N'
   and dok_f_zatwierdzony = 'T'
   and dok_numer_wlasny like 'PZ%'
   and DOK_MAG_OB_ID_W = 101317 -- id magazynu
   and dok_data_zaksiegowania between '2021-01-01' and '2021-09-30' 
group by dok_id, dok_numer_wlasny
) 
order by 2
)

select sum(ks_kwota) from kgt_ksiegowania where ks_dok_id = 9441644


select sum(ks_wart) from (
select dok_id, dok_numer_wlasny, pz_wart, ks_wart, pz_wart - ks_wart spr from (
 select dok_id, dok_numer_wlasny, sum(pdok_wartosc) pz_wart
 , (select sum(ks_kwota) from kgt_ksiegowania where ks_dok_id = dok_id) ks_wart
  from kgt_dokumenty, gmt_pozycje_dokumentow
 where pdok_dok_id = dok_id
   and dok_f_wstrzymany = 'N'
   and dok_f_zatwierdzony = 'T'
   and (dok_numer_wlasny like 'WZ%' or dok_numer_wlasny like 'RWOT%')
   and DOK_MAG_OB_ID_W = 101317 -- id magazynu
   and dok_data_zaksiegowania between '2021-01-01' and '2021-09-30' 
group by dok_id, dok_numer_wlasny
) 
order by 2
)
B:
select * from (
select tow_indeks, sum(pz_wart) spr from (
select tow_indeks, sum(pdok_wartosc) pz_wart 
  from kgt_dokumenty, gmt_pozycje_dokumentow, css_towary
 where pdok_dok_id = dok_id
   and pdok_tow_id = tow_id
   and dok_f_wstrzymany = 'N'
   and dok_f_zatwierdzony = 'T'
   and dok_numer_wlasny like 'PZ%'
   and DOK_MAG_OB_ID_W = 101317 -- id magazynu
   and dok_data_zaksiegowania between '2021-01-01' and '2021-01-31' 
group by tow_indeks
union all
select tow_indeks, - sum(pdok_wartosc) pz_wart 
  from kgt_dokumenty, gmt_pozycje_dokumentow, css_towary
 where pdok_dok_id = dok_id
   and pdok_tow_id = tow_id
   and dok_f_wstrzymany = 'N'
   and dok_f_zatwierdzony = 'T'
   and (dok_numer_wlasny like 'WZ%' or dok_numer_wlasny like 'RWOT%')
   and DOK_MAG_OB_ID_W = 101317 -- id magazynu
   and dok_data_zaksiegowania between '2021-01-01' and '2021-01-31' 
group by tow_indeks)
group by tow_indeks) where spr != 0
</pre>



#### 2021.09.16 odpisałem na maila różnica na 150 zł w sytczniu w nap catering PZ a faktura 

#### 2021.05.31
#### rożnica RWOT a OTW
<pre>
select tow_nazwa, sum(pdok_ilosc_dys) ilosc_dys, sum(pdok_ilosc_rea) ilosc_rea, sum(pdok_wartosc) wart_z_RWOT
, (select count(1) ilosc from stt_Srodki_dane , kgt_dokumenty where dok_sdn_s_id = sdn_s_id and dok_sdn_id = sdn_id
    and sdn_nazwa = tow_nazwa
    and dok_rdok_kod = 'OTW' and to_char(dok_data_zaksiegowania, 'YYYY-MM') = '2021-03') ilosc_OTW
, (select sum(sdn_wartosc_nabycia) wart_z_OTW from stt_Srodki_dane , kgt_dokumenty where dok_sdn_s_id = sdn_s_id and dok_sdn_id = sdn_id
    and sdn_nazwa = tow_nazwa
    and dok_rdok_kod = 'OTW' and to_char(dok_data_zaksiegowania, 'YYYY-MM') = '2021-03') wart_z_OTW    
from GMT_POZYCJE_DOKUMENTOW gm, kgt_dokumenty, css_towary 
 where pdok_dok_id = dok_id
   and pdok_tow_id = tow_id
   and dok_rdok_kod = 'RWOT'
   and to_char(dok_data_zaksiegowania, 'YYYY-MM') = '2021-03' 
   group by tow_nazwa order by tow_nazwa
</pre>

,'KSPZUOPM'

38 1140 2004 0000 3702 4511 9306

Nowe raporty:
EGERIA GM > 20 Wykaz PZ i KPZ

<pre>

----- 2020.04.09
-- PZ w danym miesiący bz PK w firmie docelowej
-- !!! ważne sprawdzenie 
begin
eap_globals.USTAW_konsolidacje('T');
end;
-- możliwe że mają tylko pozycje z OT
select frm_nazwa, mag_kod_nr, dok_numer_wlasny, dok_Data_zaksiegowania, pdok_tow_id, pdok_wartosc
, (select (1) from CSS_KODY_KLASYFIKACJI where kk_kl_id = 100061 and kk_tow_id = pdok_tow_id) czy_ST
 from kgt_dokumenty, GMT_POZYCJE_DOKUMENTOW, eat_firmy, gmt_magazyny
where 1=1
and dok_mag_ob_id_w = mag_ob_id
and dok_frm_id = frm_id
and pdok_dok_id = dok_id
and dok_numer_wlasny like 'PZ%'
and to_char(dok_data_zaksiegowania,'YYYY-MM') = '2020-04'
and dok_sdok_stan = 'ZTK'
and dok_id not in (
select dok_id from(
select frm_nazwa, dok_numer_wlasny, to_char(dok_data_zaksiegowania, 'YYYY-MM') okres, substr(knt_pelny_numer,5,4) sk_kod, sum(ks_kwota), substr(ks_tresc, 0,  Instr(ks_tresc, ';')-1) dok_id
from kgt_dokumenty, eat_firmy, kgt_ksiegowania, kg_konta  
where dok_frm_id = frm_id
and dok_def_2 like '%PK-ROZ.%'
and ks_dok_id = dok_id
and ks_knt_wn = knt_id
group by frm_nazwa, dok_numer_wlasny, to_char(dok_data_zaksiegowania, 'YYYY-MM'), substr(knt_pelny_numer,5,4), ks_tresc
order by frm_nazwa, substr(knt_pelny_numer,5,4))
) order by 1, 2, 3


-- 2020-01-13
-- Sprawdzenie po kontach ile PZ i KPZ w następnym miesiącu po MA i jak wgrał faktury Tomek po WN
-- daty m1 im2 to okres dla PZ tylko po WN a n1 i n2 to następny okres dla KPZ i faktury
begin
eap_globals.USTAW_konsolidacje('T');
end;

select frm_nazwa, sum(wn) wn, sum(ma) ma from (
select null frm_nazwa, 0 wn, 0 ma from dual 
union all
select frm_nazwa, 0, ks_kwota wn  
from kgt_ksiegowania, kgt_dokumenty, kg_konta, eat_firmy
where ks_dok_id = dok_id
and ks_frm_id = frm_id
and dok_data_zaksiegowania between :m1 and :m2
and (ks_knt_ma = knt_id)
and ks_tresc like '%;PZ%'
AND (ks_f_zaksiegowany = 'T' or ks_f_symulacja = 'T')
--AND ks_rodzaj = 'PK'
and knt_pelny_numer like '301-01-011100'
union all
select frm_nazwa, ks_kwota wn, 0 ma  
from kgt_ksiegowania, kgt_dokumenty, kg_konta, eat_firmy
where ks_dok_id = dok_id
and ks_frm_id = frm_id
and dok_data_zaksiegowania between :n1 and :n2
and (ks_knt_wn = knt_id)
AND (ks_f_zaksiegowany = 'T' or ks_f_symulacja = 'T')
--AND ks_rodzaj = 'PK'
and knt_pelny_numer like '301-01-011100'
union all
select frm_nazwa, 0, ks_kwota wn  
from kgt_ksiegowania, kgt_dokumenty, kg_konta, eat_firmy
where ks_dok_id = dok_id
and ks_frm_id = frm_id
and dok_data_zaksiegowania between :n1 and :n2
and (ks_knt_ma = knt_id)
and ks_tresc like '%;KPZ%'
AND (ks_f_zaksiegowany = 'T' or ks_f_symulacja = 'T')
--AND ks_rodzaj = 'PK'
and knt_pelny_numer like '301-01-011100'
)
group by frm_nazwa



select * from kgt_dokumenty where dok_id in (
select * from (
select dok_id from(
select frm_nazwa, dok_numer_wlasny, to_char(dok_data_zaksiegowania, 'YYYY-MM') okres, substr(knt_pelny_numer,5,4) sk_kod, sum(ks_kwota), substr(ks_tresc, 0,  Instr(ks_tresc, ';')-1) dok_id
from kgt_dokumenty, eat_firmy, kgt_ksiegowania, kg_konta  
where dok_frm_id = frm_id
and dok_def_2 like '%PK-ROZ.%'
and ks_dok_id = dok_id
and ks_knt_wn = knt_id
group by frm_nazwa, dok_numer_wlasny, to_char(dok_data_zaksiegowania, 'YYYY-MM'), substr(knt_pelny_numer,5,4), ks_tresc
--order by frm_nazwa, substr(knt_pelny_numer,5,4))
)) where dok_id not in (
select dok_id
 from kgt_dokumenty
where dok_numer_wlasny like 'PZ%'
and to_char(dok_data_zaksiegowania,'YYYY-MM') = '2020-03'
and dok_sdok_stan = 'ZTK'
))




begin
eap_globals.USTAW_firme(300304);
eap_globals.USTAW_konsolidacje('N');
end;




select tow_nazwa, pdok_ilosc_rea, pdok_wartosc 
 from kgt_dokumenty, gmt_pozycje_dokumentow, css_towary
where pdok_dok_id = dok_id and pdok_tow_id = tow_id
and dok_numer_wlasny like 'PZ%'
and to_char(dok_data_zaksiegowania,'YYYY-MM') = '2020-03'
and pdok_tow_id in
(
select tow_id from css_towary where tow_id in (
select kk_tow_id from CSS_KODY_KLASYFIKACJI where kk_kl_id = 100061
)
)



----- 2020.04.08
begin
eap_globals.USTAW_konsolidacje('T');
end;



select count(1) from kgt_ksiegowania where ks_dok_id in (
select dok_id from kgt_dokumenty where dok_def_2 like '%PK-ROZ%')


nap_gm_tools




select * from kgt_ksiegowania where ks_dok_id in (
select dok_id
from kgt_dokumenty, eat_firmy  
where dok_frm_id = frm_id
and dok_def_2 like '%PK-ROZ.%'
)

select frm_id, frm_nazwa, dok_numer_wlasny, to_char(dok_data_zaksiegowania, 'YYYY-MM') okres, count(1)
from kgt_dokumenty, eat_firmy, kgt_ksiegowania
where dok_frm_id = frm_id
and dok_def_2 like '%PK-ROZ.%'
and ks_dok_id = dok_id
group by frm_id, frm_nazwa, dok_numer_wlasny, to_char(dok_data_zaksiegowania, 'YYYY-MM') 
order by 2

select frm_nazwa, dok_numer_wlasny, to_char(dok_data_zaksiegowania, 'YYYY-MM') okres, substr(knt_pelny_numer,5,4) sk_kod, sum(ks_kwota)
from kgt_dokumenty, eat_firmy, kgt_ksiegowania, kg_konta  
where dok_frm_id = frm_id
and dok_def_2 like '%PK-ROZ.%'
and ks_dok_id = dok_id
and ks_knt_wn = knt_id
group by frm_nazwa, dok_numer_wlasny, to_char(dok_data_zaksiegowania, 'YYYY-MM'), substr(knt_pelny_numer,5,4) 
order by frm_nazwa, substr(knt_pelny_numer,5,4)

</pre>
