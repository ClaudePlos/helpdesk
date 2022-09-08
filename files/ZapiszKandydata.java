@Override
    public Pracownik ZapiszKandydata(Pracownik p_prc) 
    {
        
        frmUstawFrmId(1);
        NuprProces defProcesu = defUprawnienBean.getDefProcesu("HR_ZMIANA_DANYCH");
   
        Boolean fNowy = true; 
        
        if ( p_prc.getPrcId() != null)
            fNowy = false; 
        
        WybranyPlik plikKwestionariusz = null;
        WybranyPlik plikPass = null;
        WybranyPlik plikDocLeg = null;

        Set<NiepelnosprawnoscVO> npDoZapisania;
        Set<AdresVO> adresyDoZapisania;

        npDoZapisania = p_prc.getNiepelnosprawnosci();
        adresyDoZapisania = p_prc.getAdresy();
        
        if ( fNowy ) {
            p_prc.setAdresy(null);
            p_prc.setNiepelnosprawnosci(null);
        }

        if (p_prc.getSkany() == null) {
            throw new LoggableEJBException("Bład danych - nie przekazano skanów");
        }
        // sprawdzam czy jest skan
        for (WybranyPlik plik : p_prc.getSkany()) {
            if (plik.kodDokumentu.equals( HR_Consts.PODTYP_DOK_FORMULARZ_OSOBOWY )) {
                plikKwestionariusz = plik;
            }
            
            if (plik.kodDokumentu.equals( HR_Consts.PODTYP_DOK_PASZPORT )) {
                plikPass = plik;
            }
            
            if (plik.kodDokumentu.equals( HR_Consts.PODTYP_DOK_LEG_POBYT )) {
                plikDocLeg = plik;
            }  
        }

        if (plikKwestionariusz == null && fNowy) {
            throw new LoggableEJBException("Błąd - nie załączono skanu kwestionariusza");
        }

        Boolean l_nowy = false;

        if (p_prc.getPrcId() == null) {
            l_nowy = true;
        }

        // sprawdzamy czy nowy
            /*
         * Pracownik l_prc; if (p_prc.getPrcId()>0) { // jest wiec pobieramy
         * l_prc = em.find(Pracownik.class, p_prc.getPrcId()); } else l_prc =
         * p_prc;
         */
        // uzupelniuamy pola
        if (p_prc.getPrcId() != null && p_prc.getPrcId() == 0) {
            p_prc.setPrcId(null);
        }
        if (p_prc.getPrc_numer() == null) {
            p_prc.setPrc_numer(Long.valueOf(0));
        }
        if (p_prc.getPrc_numer() <= 0) {
            // szukamy nastepnego prc_numer
            BigDecimal ret = (BigDecimal) em.createNativeQuery("select max(prc_numer) from ek_pracownicy").getSingleResult();
            p_prc.setPrc_numer((Long) ret.longValue() + 1);
        }
        if (p_prc.getPrc_dg_kod_ek() == null) {
            p_prc.setPrc_dg_kod_ek("EK_KA");
        }
        if (p_prc.getPrc_dg_kod_ek() == null) {
            p_prc.setPrc_dg_kod_ek("EK_KA");
        }

        if (p_prc.getPrc_dg_kod_pl() == null) {
            p_prc.setPrc_dg_kod_pl("PL_KA");
        }
        if (p_prc.getPrc_dg_kod_pl().length() == 0) {
            p_prc.setPrc_dg_kod_pl("PL_KA");
        }

        p_prc.setPrc_karta_pobytu("N");
        p_prc.setPrcNipDane(p_prc.getPrc_nip());
        // robie na upper
        p_prc.setPrc_imie(p_prc.getPrc_imie().toUpperCase());
        p_prc.setPrc_nazwisko(p_prc.getPrc_nazwisko().toUpperCase());

        if (p_prc.getPrc_imie_matki() != null) {
            p_prc.setPrc_imie_matki(p_prc.getPrc_imie_matki().toUpperCase());
        }

        if (p_prc.getPrc_imie_ojca() != null) {
            p_prc.setPrc_imie_ojca(p_prc.getPrc_imie_ojca().toUpperCase());
        }

        if (p_prc.getPrc_nazwisko_rod() != null) {
            p_prc.setPrc_nazwisko_rod(p_prc.getPrc_nazwisko_rod().toUpperCase());
        }

        if (!l_nowy) {
            Pracownik merge = em.merge(p_prc);
            em.persist(merge);
            p_prc = merge;
        } else {
            em.persist(p_prc);
        }

        try {
            em.flush();
        } catch (PersistenceException e) {
            throw new LoggableEJBException("Nie udało się zapisać pracownika", e);
        }

        /**
         * Adres *
         */
        /// kasuje adresy
        em.createQuery(" delete from AdresVO where prcId = :prcId").setParameter("prcId", p_prc.getPrcId()).executeUpdate();
        /**
         * dodaje adresy *
         */
        for (AdresVO adres : adresyDoZapisania) {
            adres.setId(null);
            adres.setPrcId(p_prc.getPrcId());
            adres.setLp(new Long(1));
            adres.setTypUlicy("ul");
            adres.setZatwierdzony("T");
            adres.setAktualne("T");
            adres.setPoczta(adres.getMiejscowosc());
            adres.setKlKod(null);
            adres = em.merge(adres);
            em.persist(adres);
        }

        // sprawdzamy niepelnosprawnosc

        Date data2000 = new Date();
        Calendar cal = Calendar.getInstance();
        cal.set(2000, 1, 1);
        data2000 = cal.getTime();
        // kasujemy np tego pracownika
        // em.createNativeQuery("delete from ek_niepelnosprawnosci where np_prc_id=:prc_id and np_data_od=:data_od").setParameter("prc_id", p_prc.getPrcId()).setParameter("data_od",data2000).executeUpdate();
        for (NiepelnosprawnoscVO npz : npDoZapisania) {
            if ( npz.getId() == null )
            {
                // sprawdzam czy jest zaswiadczenie
                if (npz.getPlikSkanu() == null) {
                    throw new LoggableEJBException("Nie załączono skanu zaświadczenia o niepełnosprawności");
                }

                WybranyPlik skanOrzeczenia = npz.getPlikSkanu();
                npz.setNp_prc_id(p_prc.getPrcId());
                npz.setNp_data_wplywu( new Date());
                npz = em.merge(npz);
                /*
                 * if ( npz.getId()>0 ) np = (NiepelnosprawnoscVO)
                 * em.find(NiepelnosprawnoscVO.class, np.getId()); else np = new
                 * NiepelnosprawnoscVO();
                 *
                 * np.setNp_kod_niepelnosprawnosci(np.getNp_kod_niepelnosprawnosci());
                 * np.setNp_prc_id( p_prc.getPrcId() );
                 */

                //np.setNp_data_od(data2000);
                //np.setNp_data_wplywu(data2000);

                /*
                 * if (np.getNp_kod_niepelnosprawnosci() != 0) em.persist(np); else
                 * em.remove(np);
                 *
                 */

                em.persist(npz);

                try {
                    em.flush();
                } catch (PersistenceException e) {
                    throw new LoggableEJBException("Nie udało się zapisać danych niepełnosprawności", e);
                }

                //NiepelnosprawnoscVO np2 = (NiepelnosprawnoscVO) em.createQuery("from NiepelnosprawnoscVO where np_prc_id=:prc_id").setParameter("prc_id",p_prc.getPrcId()).getSingleResult();
                //p_prc.setNiepelnosprawnosc(np2);

                /**
                 * skan zaswiadczenia *
                 */
                DokumentVO dok = new DokumentVO();

                dok.setNazwa("orz_niep_" + p_prc.getPrc_nazwisko() + " " + p_prc.getPrc_imie());
                dok.setOpis("Orzeczenie o niepełnosprawności: " + p_prc.getPrc_nazwisko() + " " + p_prc.getPrc_imie());
                dok.setTyp(DokumentVO.TYP_DOKUMENT);

                DokOpisVO opisTyp = new DokOpisVO(DokOpisVO.TYP_RELACJI_JEST);
                opisTyp.setTypDokumentu( HR_Consts.TYP_DOK_DOKUMENTACJA_KADROWA ) ;
                opisTyp.setPodtypDokumentu( HR_Consts.PODTYP_DOK_ZASW_NIEP ) ;
                opisTyp.setPrcId(p_prc.getPrcId());
                dok.getOpisy().add(opisTyp);

                DokOpisVO opisDotyczy = new DokOpisVO(DokOpisVO.TYP_RELACJI_DOTYCZY);
                opisDotyczy.setPrcId(p_prc.getPrcId());
                dok.getOpisy().add(opisDotyczy);
                
                DokOpisVO opisDefProc = new DokOpisVO(DokOpisVO.TYP_RELACJI_DOTYCZY );
                opisDefProc.setDefProcesuId( defProcesu.getId() );
                dok.getOpisy().add( opisDefProc );

                // wersja skan
                DokWersjaVO wer = new DokWersjaVO(DokWersjaVO.TYP_WERSJI_SKAN, pobierzUzytkownika(), skanOrzeczenia);

                dok.getWersje().add(wer);
                dokServiceBean.dodajLubAktualizujDokument(dok);
            }

        } // koniec zapisu niepelnosprawnosci

        // zapisuje skan
        if ( fNowy ) {
            DokumentVO dok = new DokumentVO();

            dok.setNazwa("kwos_" + p_prc.getPrc_nazwisko() + " " + p_prc.getPrc_imie());
            dok.setOpis("Kwestionariusz osobowy pracownika: " + p_prc.getPrc_nazwisko() + " " + p_prc.getPrc_imie());
            dok.setTyp(DokumentVO.TYP_DOKUMENT);

            DokOpisVO opisTyp = new DokOpisVO(DokOpisVO.TYP_RELACJI_JEST);
            opisTyp.setTypDokumentu( HR_Consts.TYP_DOK_DOKUMENTACJA_KADROWA ) ;
            opisTyp.setPodtypDokumentu( HR_Consts.PODTYP_DOK_FORMULARZ_OSOBOWY ) ;        
            opisTyp.setPrcId(p_prc.getPrcId());
            dok.getOpisy().add(opisTyp);

            DokOpisVO opisDotyczy = new DokOpisVO(DokOpisVO.TYP_RELACJI_DOTYCZY);
            opisDotyczy.setPrcId(p_prc.getPrcId());
            dok.getOpisy().add(opisDotyczy);
            
             DokOpisVO opisDefProc = new DokOpisVO(DokOpisVO.TYP_RELACJI_DOTYCZY );
                opisDefProc.setDefProcesuId( defProcesu.getId() );
                dok.getOpisy().add( opisDefProc );


            // wersja skan
            DokWersjaVO wer = new DokWersjaVO(DokWersjaVO.TYP_WERSJI_SKAN, pobierzUzytkownika(), plikKwestionariusz);

            dok.getWersje().add(wer);
            //try {
            dokServiceBean.dodajLubAktualizujDokument(dok);
            
            if (!p_prc.getPrc_obywatelstwo().equals("Polskie")){
                DokumentVO dokPass = new DokumentVO();

                dokPass.setNazwa("pasz_" + p_prc.getPrc_nazwisko() + " " + p_prc.getPrc_imie());
                dokPass.setOpis("Paszport pracownika: " + p_prc.getPrc_nazwisko() + " " + p_prc.getPrc_imie());
                dokPass.setTyp(DokumentVO.TYP_DOKUMENT);

                DokOpisVO opisTypPass = new DokOpisVO(DokOpisVO.TYP_RELACJI_JEST);
                opisTypPass.setTypDokumentu( HR_Consts.TYP_DOK_DOKUMENTACJA_KADROWA ) ;
                opisTypPass.setPodtypDokumentu( HR_Consts.PODTYP_DOK_PASZPORT ) ;        
                opisTypPass.setPrcId(p_prc.getPrcId());
                dokPass.getOpisy().add(opisTypPass);

                DokOpisVO opisDotyczyPass = new DokOpisVO(DokOpisVO.TYP_RELACJI_DOTYCZY);
                opisDotyczyPass.setPrcId(p_prc.getPrcId());
                dokPass.getOpisy().add(opisDotyczyPass);

                DokOpisVO opisDefProcPass = new DokOpisVO(DokOpisVO.TYP_RELACJI_DOTYCZY );
                opisDefProcPass.setDefProcesuId( defProcesu.getId() );
                dokPass.getOpisy().add( opisDefProcPass );


                // wersja skan
                DokWersjaVO werPass = new DokWersjaVO(DokWersjaVO.TYP_WERSJI_SKAN, pobierzUzytkownika(), plikPass);

                dokPass.getWersje().add(werPass);
                //try {
                dokServiceBean.dodajLubAktualizujDokument(dokPass);
                
                // leg pobyt w PL
                DokumentVO dokDocLeg = new DokumentVO();

                dokDocLeg.setNazwa("dokleg_" + p_prc.getPrc_nazwisko() + " " + p_prc.getPrc_imie());
                dokDocLeg.setOpis("Legalny pobyt pracownika: " + p_prc.getPrc_nazwisko() + " " + p_prc.getPrc_imie());
                dokDocLeg.setTyp(DokumentVO.TYP_DOKUMENT);

                DokOpisVO opisTypDocLeg = new DokOpisVO(DokOpisVO.TYP_RELACJI_JEST);
                opisTypDocLeg.setTypDokumentu( HR_Consts.TYP_DOK_DOKUMENTACJA_KADROWA ) ;
                opisTypDocLeg.setPodtypDokumentu( HR_Consts.PODTYP_DOK_LEG_POBYT ) ;        
                opisTypDocLeg.setPrcId(p_prc.getPrcId());
                dokDocLeg.getOpisy().add(opisTypDocLeg);

                DokOpisVO opisDotyczyDocLeg = new DokOpisVO(DokOpisVO.TYP_RELACJI_DOTYCZY);
                opisDotyczyDocLeg.setPrcId(p_prc.getPrcId());
                dokDocLeg.getOpisy().add(opisDotyczyDocLeg);

                DokOpisVO opisDefProcDocLeg = new DokOpisVO(DokOpisVO.TYP_RELACJI_DOTYCZY );
                opisDefProcDocLeg.setDefProcesuId( defProcesu.getId() );
                dokDocLeg.getOpisy().add( opisDefProcDocLeg );


                // wersja skan
                DokWersjaVO werDocLeg = new DokWersjaVO(DokWersjaVO.TYP_WERSJI_SKAN, pobierzUzytkownika(), plikDocLeg);

                dokDocLeg.getWersje().add(werDocLeg);
                //try {
                dokServiceBean.dodajLubAktualizujDokument(dokDocLeg);
            }
        }
        
        //}
            /*
         * catch ( Exception e ) { Object a = e.getCause(); if ( e instanceof
         * EJBException) { EJBException ee = (EJBException) e ; Object casue =
         * ee.getCausedByException();
         *
         * }
         * throw new LoggableEJBException("Nie udaĹo siÄ zapisaÄ kandydata"
         * ,e );
            }
         */

        return pobierzPracownika(p_prc.getPrcId());
        /*
         * }
         * catch (Exception e) { throw new LoggableEJBException("Nie udaĹo siÄ
         * zapisaÄ kandydata" ,e ); }
         *
         */

    }
