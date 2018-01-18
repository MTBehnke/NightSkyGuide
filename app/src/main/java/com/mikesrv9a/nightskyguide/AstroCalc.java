package com.mikesrv9a.nightskyguide;

// J2000.0 Epoch = JD 2451545.0 = 1/1/2000 12:00:00 UT
// J2010.0 Epoch = JD 2455196.5 = 12/31/2009 00:00:00 UT
// Unix/Java Epoch = JD 2440587.5 (1/1/1970 00:00:00 UT
// Sidereal formulas adapted from http://aa.usno.navy.mil/faq/docs/GAST.php
// Alt/Az formulas adapted from http://aa.usno.navy.mil/faq/docs/Alt_Az.php
// Planet position calculations based on Practical Astronomy with your Calculator or Spreadsheet, Fourth Edition
//      by Peter Duffett-Smith and Jonathan Zwart
// Additional info at https://en.wikipedia.org/wiki/Sidereal_time
// Additional info at http://www.stargazing.net/kepler/altaz.html


public class AstroCalc {

    // these values are from Table 8: Elements of the planetary orbits at epoch 2010.0 (page 123)
    static String[] planetName = {"Earth", "Mercury", "Venus", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"};
    static double[] orbitPeriod = {0.999996,0.24085,0.615207,1.880765,11.857911,29.310579,84.039492,165.84539};  // Tp: period of orbit, tropical years
    static double[] epochLong = {99.556772,75.5671,272.30044,109.09646,337.917132,172.398316,356.135400,326.895127};  // ɛ: longitude at epoch, degrees
    static double[] periLong = {103.2055,77.612,131.54,336.217,14.6633,89.567,172.884833,23.07};  // ϖ: longitude at perihelion, degrees
    static double[] orbitEcc = {0.016671,0.205627,0.006812,0.093348,0.048907,0.053853,0.046321,0.010483};  // e: eccentricity of the orbit
    static double[] majorAxis = {0.999985,0.387098,0.723329,1.523689,5.20278,9.51134,19.21814,30.1985};  // a: semi-major axis of the orbit, AU
    static double[] inclination = {0.0,7.0051,3.3947,1.8497,1.3035,2.4873,0.773059,1.7673};  // i: orbital inclination, degrees
    static double[] ascendNodeLong = {0.0,48.449,76.769,49.632,100.595,113.752,73.926961,131.879};  // Ω: longitude of the ascending node, degrees
    static double[] angularDia0 = {0.0,6.74,16.92,9.36,196.74,165.60,65.80,62.20};  //  θ0: angular diameter at 1 AU, arc-seconds
    static double[] visualMag0 = {0.0,-0.42,-4.40,-1.52,-9.40,-8.88,-7.19,-6.87}; //  V0: visual magnitude at 1 AU


    // THE FOLLOWING METHODS ARE USED IN PLANETARY CALCULATIONS
    // return days since J2010.0, input = millis since Unix/Java epoch
    public static double daysSinceJ2010(long javaMillis) {
        return (double)javaMillis / 86400000 - 14609;  // less (J2010 - Java Epoch)
    }

    // return mean anomoly of planet, M, in degrees
    public static double meanAnomoly(int planet, double daysSinceEpoch) {
        double Ndeg = (360/365.242191) * (daysSinceEpoch / orbitPeriod[planet]);  // total degrees orbited since epoch
        Ndeg = Ndeg - 360 * Math.floor(Ndeg/360);  // bring result into range of 0 to 360 degrees
        double Mdeg = Ndeg + epochLong[planet] - periLong[planet];
        return Mdeg;
    }

    // return true anomoly of a planet, v, in degrees
    public static double trueAnomoly(int planet, double meanAnomoly) {
        double Vdeg = meanAnomoly + (360/Math.PI) * orbitEcc[planet]*Math.sin(Math.toRadians(meanAnomoly));
        Vdeg = Vdeg - 360 * Math.floor(Vdeg/360);  // bring result into range of 0 to 360 degrees
        return Vdeg;
    }

    // return heliocentric longitude of a planet, l, in degrees
    public static double helioLong(int planet, double trueAnomoly) {
        double hLong = trueAnomoly + periLong[planet];
        hLong = hLong - 360 * Math.floor(hLong/360);  // bring result into range of 0 to 360 degrees
        return hLong;
    }

    // return length of radius vector, r, in AU
    public static double radiusVector(int planet, double trueAnomoly) {
        double r = majorAxis[planet] * (1 - Math.pow(orbitEcc[planet],2)) / (1 + (orbitEcc[planet] * Math.cos(Math.toRadians(trueAnomoly))));
        return r;
    }

    // return heliocentric latitude of a planet, ψ, in degrees
    public static double helioLat(int planet, double helioLong){
        double helLat = Math.toDegrees(Math.asin(Math.sin(Math.toRadians(helioLong-ascendNodeLong[planet])) * Math.sin(Math.toRadians(inclination[planet]))));
        return helLat;
    }

    // return projected heliocentric longitude, l', in degrees
    public static double projHelioLong(int planet, double helioLong){
        double x = Math.cos(Math.toRadians(helioLong-ascendNodeLong[planet]));
        double y = Math.sin(Math.toRadians(helioLong-ascendNodeLong[planet])) * Math.cos(Math.toRadians(inclination[planet]));
        double projHelLong = Math.toDegrees(Math.atan2(y,x));
        if (projHelLong<0) {projHelLong = projHelLong + 360;}
        return projHelLong + ascendNodeLong[planet];
    }

    // return projected radius vector, r', in AU
    public static double projRadiusVector(double radiusVector, double helioLat){
        double projRadVector = radiusVector * Math.cos(Math.toRadians(helioLat));
        return projRadVector;
    }

    // return geocentric ecliptic longitude, λ, in degrees
    public static double geoEclLong(int planet, double projHelLong, double projRadVector, double earthHelLong, double earthRadVector){
        double geoEclLong;
        if (planet < 3) {     // inner planets
            geoEclLong = 180 + earthHelLong + Math.toDegrees(Math.atan((projRadVector * Math.sin(Math.toRadians(earthHelLong - projHelLong))) /
                    (earthRadVector - projRadVector * Math.cos(Math.toRadians(earthHelLong - projHelLong)))));
        }
        else {    // outer planets
            geoEclLong = projHelLong + Math.toDegrees(Math.atan((earthRadVector * Math.sin(Math.toRadians(projHelLong - earthHelLong))) /
                    (projRadVector - earthRadVector * Math.cos(Math.toRadians(projHelLong - earthHelLong)))));
        };
        if (geoEclLong >= 360) { geoEclLong = geoEclLong - 360;}
        else if (geoEclLong < 0) { geoEclLong = geoEclLong + 360;}
        return geoEclLong;
    }

    // return geocentric ecliptic latitude, ß, in degrees
    public static double geoEclLat(double projHelLong, double projRadVector, double earthHelLong, double earthRadVector, double geoEclLong, double helioLat) {
        double geoEclLat = Math.toDegrees(Math.atan((projRadVector * Math.tan(Math.toRadians(helioLat)) * Math.sin(Math.toRadians(geoEclLong-projHelLong))) /
                (earthRadVector * Math.sin(Math.toRadians(projHelLong - earthHelLong)))));
        return geoEclLat;
    }

    // return obliquity of the ecliptic, ε, in degrees, input is days since J2000
    public static double obliqEclip(double epochDays) {
        double centuries = epochDays / 36525.0;
        double temp = 46.815*centuries + 0.0006*Math.pow(centuries,2) - 0.00181*Math.pow(centuries,3);
        double obliqEclip = 23.439292 - temp / 3600;
        return obliqEclip;
    }

    // return right ascension from ecliptic longitude and latitude, in degrees
    public static double raFromEclip(double obliqEclip, double eclipLong, double eclipLat) {
        double ra = Math.toDegrees(Math.atan2((Math.sin(Math.toRadians(eclipLong)) * Math.cos(Math.toRadians(obliqEclip)) -
                        Math.tan(Math.toRadians(eclipLat) * Math.sin(Math.toRadians(obliqEclip)))), Math.cos(Math.toRadians(eclipLong))));
        if (ra<0) {ra = ra + 360;}
        return ra;
    }

    // return declination from exliptic longitude and latitude, in degrees
    public static double decFromEclip(double obliqEclip, double eclipLong, double eclipLat) {
        double dec = Math.toDegrees(Math.asin(Math.sin(Math.toRadians(eclipLat)) * Math.cos(Math.toRadians(obliqEclip)) +
                Math.cos(Math.toRadians(eclipLat)) * Math.sin(Math.toRadians(obliqEclip)) * Math.sin(Math.toRadians(eclipLong))));
        return dec;
    }

    // return distance of planet to Earth, in AU
    public static double distPlanet(double projHelLong, double projRadVector, double earthHelLong, double earthRadVector, double helioLat) {
        double distance = Math.sqrt(Math.pow(earthRadVector,2) + Math.pow(projRadVector,2) -
                (2*earthRadVector*projRadVector*Math.cos(Math.toRadians(projHelLong-earthHelLong))*Math.cos(Math.toRadians(helioLat))));
        return distance;
    }

    // return apparent angular size of a planet from Earth, in arc-sec
    public static double sizePlanet(Integer planet, double distPlanet) {
        return angularDia0[planet] / distPlanet;
    }

    // return apparent magnitude of a planet from Earth
    public static double magPlanet(Integer planet, double geoEclipLong, double helioLong, double radiusVector, double distPlanet) {
        double phase = 0.5*(1+Math.cos(Math.toRadians(geoEclipLong-helioLong)));
        double magnitude = 5 * Math.log10(radiusVector*distPlanet/Math.sqrt(phase)) + visualMag0[planet];
        return magnitude;
    }

    // THE FOLLOWING METHODS ARE USED TO CALCULATE WHERE AN OBJECT IS IN THE SKY
    // return days since J2000.0, input = millis since Unix/Java epoch
    public static double daysSinceJ2000(long javaMillis){
        return (double)javaMillis / 86400000 - 10957.5;  // less (J2000 - Java Epoch)
    }

    // return Greenwich Mean Sidereal Time (GMST) from daysSinceJ2000 - in degrees
    // can replace 24 with 2*pi for radians or 360 for degrees
    public static double greenwichST(double daysSinceJ2000) {
        double num =  (18.697374558 + 24.06570982441908 * daysSinceJ2000) / 24;
        double numFloor = Math.floor(num); // floor rounds down to nearest integer value
        return (num - numFloor) * 360;
    }

    // return Local Sidereal Time from greenwich and Local Longitude - in degrees
    public static double localST(double greenwichST, double locLong) {
        double lst = greenwichST + locLong;
        if
            (lst > 360) {lst = lst - 360;}
        else if
            (lst < 0) {lst = lst + 360;}
        return lst;
    }

    // return Hour Angle from localST, Local Longitude and RA of object
    public static double hourAngle(double localST, double rA) {
        double ha = localST - rA;
        if
            (ha < 0)  {ha = ha + 360;}
        return ha;
    }

    // return Altitude of object
    public static double dsoAlt(double dec, double lat, double ha) {
        double sinAlt = (Math.sin(Math.toRadians(dec))*Math.sin(Math.toRadians(lat)))+
                (Math.cos(Math.toRadians(dec)) * Math.cos(Math.toRadians(lat)) *
                Math.cos(Math.toRadians(ha)));
        double alt = Math.toDegrees(Math.asin(sinAlt));
        return alt;
    }

    // return Azimuth of object
    public static double dsoAz(double dec, double lat, double ha, double alt) {
        double az;
        double cosAz = (Math.sin(Math.toRadians(dec))-Math.sin(Math.toRadians(alt))*
            Math.sin(Math.toRadians(lat)))/(Math.cos(Math.toRadians(alt))*
            Math.cos(Math.toRadians(lat)));
        if (Math.sin(Math.toRadians(ha)) < 0)
            {az = Math.toDegrees(Math.acos(cosAz));}
        else
            {az = 360 - Math.toDegrees(Math.acos(cosAz));}
        return az;
    }

    // return Cos(HA) when altitude = 0° to calc rise/set times - in Radians
    // if Cos(HA) < -1, then object is circumpolar
    // if Cos(HA) > 1, then object never rises (at this latitude)
    public static Double dsoOnHorizCosHA(double dec, double lat) {
        double CosHA = -((Math.sin(Math.toRadians(dec))) *
                (Math.sin(Math.toRadians(lat)))) / ((Math.cos(Math.toRadians(dec)))
                * Math.cos(Math.toRadians(lat)));
        return CosHA;
    }

    // convert degrees from double format to dms string format
    public static String convertDDToDMS(double dd) {
        int deg = (int) dd;
        int min = (int) ((dd-deg)*60.0);
        long sec =  Math.round ((dd-deg-(min/60.0))*3600);
        if(sec == 60) {sec = 0; min++;if (min == 60) {min = 0; deg ++;
            if (deg == 360) {deg = 0;}}}
        return deg + "° " + String.format("%02d",Math.abs(min)) + "' " +
                String.format("%02d",Math.abs(sec)) + "\"";
    }

    // convert degrees from double format to hms string format
    public static String convertDDToHMS (double dd) {
        dd = 24.0 * dd / 360.0;
        int hour = (int) dd;
        int min = (int) ((dd-hour)*60.0);
        long sec =  Math.round ((dd-hour-(min/60.0))*3600);
        if(sec == 60) {sec = 0; min++;if (min == 60) {min = 0; hour ++;
            if (hour == 24) {hour = 0;}}}
        return hour + "h " + String.format("%02d",min) + "m " +
                String.format("%02d",sec) + "s";
    }

    public static String getConstName (String abbr) {
        String[] constAbbrs = {"AND","ANT","APS","AQL","AQR","ARA","ARI","AUR","BOO","CAE","CAM","CAP","CAR","CAS","CEN","CEP","CET","CHA","CIR",
                "CMA","CMI","CNC","COL","COM","CRA","CRB","CRT","CRU","CRV","CVN","CYG","DEL","DOR","DRA","EQU","ERI","FOR","GEM","GRU","HER",
                "HOR","HYA","HYI","IND","LAC","LEO","LEP","LIB","LMI","LUP","LYN","LYR","MEN","MIC","MON","MUS","NOR","OCT","OPH","ORI","PAV",
                "PEG","PER","PHE","PIC","PSA","PSC","PUP","PYX","RET","SCL","SCO","SCT","SER","SEX","SGE","SGR","TAU","TEL","TRA","TRI","TUC",
                "UMA","UMI","VEL","VIR","VOL","VUL"};
        String[] constNames = {"Andromeda","Antlia","Apus","Aquila","Aquarius","Ara","Aries","Auriga","Boötes","Caelum","Camelopardalis","Capricornus",
                "Carina","Cassiopeia","Centaurus","Cepheus","Cetus","Chamaeleon","Circinus","Canis Major","Canis Minor","Cancer","Columba","Coma Berenices",
                "Corona Austrina","Corona Borealis","Crater","Crux","Corvus","Canes Venatici","Cygnus","Delphinus","Dorado","Draco","Equuleus","Eridanus",
                "Fornax","Gemini","Grus","Hercules","Horologium","Hydra","Hydrus","Indus","Lacerta","Leo","Lepus","Libra","Leo Minor","Lupus","Lynx",
                "Lyra","Mensa","Microscopium","Monoceros","Musca","Norma","Octans","Ophiuchus","Orion","Pavo","Pegasus","Perseus","Phoenix","Pictor",
                "Piscis Austrinus","Pisces","Puppis","Pyxis","Reticulum","Sculptor","Scorpius","Scutum","Serpens","Sextans","Sagitta","Sagittarius",
                "Taurus","Telescopium","Triangulum Australe","Triangulum","Tucana","Ursa Major","Ursa Minor","Vela","Virgo","Volans","Vulpecula"};
        String constName = "";
        for (int i=0; i<constAbbrs.length;i++) {
            if(constAbbrs[i].equals(abbr)) {
                constName = constNames[i];
                break;
            }
        }
        return constName;
    }

    // determine which constellation a planet is currently in
    public static String planetConst(double raP, double decP) {
        if ((constIntersect(0,22,raP,decP) & 1) == 1) {return "AQR";}
        if ((constIntersect(23,34,raP,decP) & 1) == 1) {return "ARI";}
        if ((constIntersect(35,46,raP,decP) & 1) == 1) {return "CAP";}
        if ((constIntersect(47,58,raP,decP) & 1) == 1) {return "CNC";}
        if ((constIntersect(59,82,raP,decP) & 1) == 1) {return "GEM";}
        if ((constIntersect(83,102,raP,decP) & 1) == 1) {return "LEO";}
        if ((constIntersect(103,116,raP,decP) & 1) == 1) {return "LIB";}
        if ((constIntersect(143,162,raP,decP) & 1) == 1) {return "SCO";}
        if ((constIntersect(163,175,raP,decP) & 1) == 1) {return "SGR";}
        if ((constIntersect(176,202,raP,decP) & 1) == 1) {return "TAU";}
        if ((constIntersect(203,221,raP,decP) & 1) == 1) {return "VIR";}
        if ((constIntersect(222,260,raP,decP) & 1) == 1) {return "OPH";}
        if ((constIntersect(261,281,raP,decP) & 1) == 1) {return "AUR";}
        if ((constIntersect(296,301,raP,decP) & 1) == 1) {return "CRV";}
        if ((constIntersect(302,310,raP,decP) & 1) == 1) {return "CRT";}
        if ((constIntersect(311,349,raP,decP) & 1) == 1) {return "HYA";}
        if ((constIntersect(350,377,raP,decP) & 1) == 1) {return "ORI";}
        if ((constIntersect(419,424,raP,decP) & 1) == 1) {return "SCT";}
        if ((constIntersect(425,431,raP,decP) & 1) == 1) {return "SEX";}
        if (raP < 90) {raP = raP + 360;}  // accounts for constellations that straddle each side of 0°
        if ((constIntersect(117,142,raP,decP) & 1) == 1) {return "PSC";}
        if ((constIntersect(282,295,raP,decP) & 1) == 1) {return "CET";}
        if ((constIntersect(378,418,raP,decP) & 1) == 1) {return "PEG";}
        return "";
    }

    // count constellation boundary intersections (odd count means point is within boundary)
    public static int constIntersect(int start, int end, double raP, double decP) {
        int intersections = 0;
        double raA;
        double raB;
        double decA;
        double decB;
        double raC;
        for (int row=start; row<=end; row++) {
            decA = constBoundaryDec[row];
            if (row<end) {decB = constBoundaryDec[row+1];}
                else {decB = constBoundaryDec[start];}
            if ((decA<decP && decB>=decP) || (decB<decP && decA>=decP)) {     // Declination of planet between both boundary corner declinations
                raA = constBoundaryRA[row];
                if (row<end) {raB = constBoundaryRA[row+1];}
                    else {raB = constBoundaryRA[start];}
                raC = raA - (raA-raB) * (decA-decP) / (decA-decB);
                if (raC>raP) {intersections++;}
            }
        }
        return intersections;
    }

    // Constellation boundary corner Right Ascension values (degrees).  Note, CET, PEG and PSC values less than 90° increased by 360°
    static double constBoundaryRA[] = {309.5988,309.5799,314.0811,321.5834,323.5842,323.5787,326.5802,326.587,331.5887,331.5872,342.8421,342.8496,
            342.8646,359.1021,359.1032,359.1105,346.6809,329.7702,329.6561,321.6684,321.7164,309.7439,309.6846,31.6651,26.6556,26.7445,30.5136,
            30.5305,38.07,38.103,42.6282,52.4265,52.2904,51.037,50.9462,309.6846,301.6937,301.7264,301.9159,306.8979,321.8316,321.8077,329.7702,
            329.6561,321.6684,321.7164,309.7439,140.4041,122.9212,120.5481,120.5805,118.8323,118.8714,118.9473,120.0699,120.1714,121.9157,121.993,
            140.6458,96.3725,96.4437,95.0694,95.1239,90.1249,90.1438,90.2208,99.9654,100.09,112.5605,118.2895,118.2579,121.993,121.9157,120.1714,
            120.0699,118.9473,118.8714,114.2525,114.2408,106.748,106.7177,105.7182,105.7426,162.8495,162.8759,145.3982,140.4041,140.6458,150.0842,
            150.0421,159.2382,159.2107,162.9424,162.9513,166.6808,166.6938,179.6088,179.6044,179.6036,174.3655,174.3504,174.3422,162.827,227.8529,
            221.603,221.667,215.4084,215.513,215.5336,225.5765,225.6307,236.9299,236.813,240.5717,240.4372,240.3869,227.8819,342.8496,342.8421,
            342.8214,359.098,359.097,361.6026,361.6031,363.734,363.7398,374.4147,374.4239,372.4134,372.4429,382.8973,382.8663,386.7646,386.7445,
            386.6556,391.6651,391.6151,366.6037,366.6012,366.5926,359.1032,359.1021,342.8646,240.4372,245.6912,245.8106,247.4382,247.4506,245.8229,
            245.8914,253.1556,253.2353,266.0018,269.5028,269.6255,269.8093,248.5706,248.4948,242.1528,241.9477,236.9299,236.813,240.5717,284.744,
            284.7937,275.5495,265.8002,266.0018,269.5028,269.6255,289.5963,289.7697,307.1693,306.8979,301.9159,301.7264,50.8365,50.8528,50.9462,
            51.037,52.2904,52.4265,69.4867,69.4766,73.2351,73.2123,90.2287,90.2208,90.1438,87.3936,87.3268,88.3269,88.2552,85.7548,85.7934,81.792,
            81.7985,76.2951,76.2887,71.0338,70.8522,55.3527,55.3354,174.3504,174.3655,179.6036,179.6044,194.0619,194.059,204.0288,204.0638,227.7814,
            227.8529,221.603,221.667,215.4084,215.513,194.1668,194.1329,179.0966,179.0985,174.3422,245.6026,245.5586,252.8059,252.7014,260.1958,
            260.1769,275.1733,275.2031,281.388,281.4585,275.2746,275.296,277.921,277.9392,275.3142,275.3506,269.101,269.1497,271.1496,271.2237,
            266.7237,266.7447,265.4944,265.4735,259.2221,259.2974,265.8002,266.0018,253.2353,253.1556,245.8914,245.8229,247.4506,247.4382,245.8106,
            245.6912,240.4372,240.3869,245.6384,69.4867,69.5736,72.4571,72.84,77.4845,77.6065,94.1308,94.0571,100.0457,99.9192,104.4061,104.265,
            112.7339,112.5605,100.09,99.9654,90.2208,90.2287,73.2123,73.2351,69.4766,366.6012,366.6037,391.6151,391.6651,410.9462,410.8528,410.8365,
            401.3391,401.1486,386.4659,386.4587,359.1105,359.1032,366.5926,194.1329,179.0966,179.0912,190.4044,190.3984,194.1668,162.827,162.8077,
            162.7754,164.0304,164.0079,179.0912,179.0966,179.0985,174.3422,122.8488,122.9212,140.4041,145.3982,145.3487,145.2701,162.8077,162.7754,
            164.0304,164.0079,179.0912,190.4044,190.3984,194.1668,215.513,215.5336,225.5765,225.6307,190.4173,190.4271,185.3873,185.3902,166.4792,
            163.9583,163.9777,160.2012,160.2127,155.1811,155.1992,147.6591,147.6795,141.9041,137.6366,137.6848,130.1633,130.1841,126.9269,126.9896,
            122.734,70.8522,71.0338,76.2887,76.2951,81.7985,81.792,85.7934,85.7548,88.2552,88.3269,87.3268,87.3936,90.1438,90.1249,95.1239,95.0694,
            96.4437,96.3725,96.3475,95.3478,95.2255,95.1769,89.0522,88.9656,77.7198,77.8042,71.5562,71.6021,321.5834,321.501,318.2445,318.2502,
            317.2483,317.1787,320.1884,320.1517,322.662,322.6201,327.3951,327.3199,329.461,331.3504,331.3595,343.7065,343.7091,344.4652,354.0441,
            354.0491,357.828,357.8287,361.6069,361.6061,362.6127,362.6099,363.7405,363.7398,363.734,361.6031,361.6026,359.097,359.098,342.8214,
            342.8421,331.5872,331.5887,326.587,326.5802,323.5787,323.5842,275.5495,284.7937,284.744,284.6473,280.3982,275.3991,145.3487,145.3982,
            162.8759,162.8495,162.827,162.8077,145.2701};

    // Constellation boundary corner declination values (degrees).
    static double constBoundaryDec[] = {0.4362,2.4361,2.4773,2.5394,2.5544,3.3044,3.3257,2.3257,2.3576,2.6076,2.6622,0.6622,-3.3377,-3.3042,-6.3042,
            -24.8042,-24.825,-24.904,-8.4044,-8.4603,-14.4601,-14.5631,-8.5634,10.5144,10.5432,25.6263,25.605,27.855,27.8047,31.2213,31.1865,31.1003,
            19.4343,19.4461,10.3632,-8.5634,-8.6431,-11.6762,-27.6419,-27.5913,-27.4596,-24.9597,-24.904,-8.4044,-8.4603,-14.4601,-14.5631,6.4701,
            6.6302,6.655,9.6548,9.6734,13.1732,19.6728,19.6608,27.6603,27.6419,33.1415,32.9692,11.9333,17.4329,17.4495,21.4492,21.5099,22.8431,28.0093,
            27.8913,35.3906,35.2445,35.1811,33.1812,33.1415,27.6419,27.6603,19.6608,19.6728,13.1732,13.2238,12.2239,12.3096,9.8098,9.8215,11.8214,
            -0.6622,6.3377,6.4328,6.4701,32.9692,32.9023,27.9024,27.853,22.853,22.8376,24.8376,24.8251,28.3251,28.3041,13.3041,10.3041,10.3083,
            -0.6917,-6.6917,-6.6622,-0.4743,-0.5269,-8.5267,-8.5731,-22.5728,-25.0727,-24.9951,-29.9949,-29.8896,-20.3902,-20.3516,-8.3523,-3.6026,
            -3.7242,0.6622,2.6622,8.1622,8.1958,10.6958,10.696,13.196,13.1952,21.6952,21.6766,24.4266,24.4319,33.6818,33.6453,28.6454,28.6262,
            25.6263,10.5432,10.5144,2.5979,2.6925,0.6925,-6.3074,-6.3042,-3.3042,-3.3377,-8.3523,-8.2959,-18.5452,-18.5272,-19.5272,-19.5452,-24.8781,
            -24.7961,-30.2123,-30.0607,-30.0182,-37.0175,-45.5164,-45.7671,-42.2675,-42.3367,-29.8378,-29.8896,-20.3902,-20.3516,-11.8664,-15.8328,
            -15.9436,-16.0619,-30.0607,-30.0182,-37.0175,-36.7785,-45.2775,-45.09,-27.5913,-27.6419,-11.6762,-1.3029,0.447,10.3632,19.4461,19.4343,
            31.1003,30.9219,30.2552,30.2123,28.7124,28.5092,28.0093,22.8431,22.8765,18.0435,18.0314,12.5319,12.5622,15.5619,15.6101,16.1101,16.1755,
            15.6755,15.7365,0.2375,0.4037,-1.3462,-0.6917,10.3083,10.3041,13.3041,13.3225,14.3225,14.3605,7.3606,7.5254,-0.4743,-0.5269,-8.5267,
            -8.5731,-22.5728,-22.6774,-11.6774,-11.6958,-6.6958,-6.6917,-0.2964,3.7034,3.7852,12.618,12.7062,14.2061,14.3875,12.0543,12.1288,6.3792,
            6.3048,4.5549,4.5866,3.0867,3.055,0.0552,-0.0206,-4.0203,-3.996,-9.9956,-10.0502,-11.7168,-11.7319,-10.0654,-10.1404,-16.14,-16.0619,
            -30.0607,-30.2123,-24.7961,-24.8781,-19.5452,-19.5272,-18.5272,-18.5452,-8.2959,-8.3523,-3.6026,-3.5462,30.9219,36.2547,36.2218,52.7196,
            52.6655,56.1648,55.9658,53.9662,53.8938,49.8946,49.841,44.3418,44.2436,35.2445,35.3906,27.8913,28.0093,28.5092,28.7124,30.2123,30.2552,
            0.6925,2.6925,2.5979,10.5144,10.3632,0.447,-1.3029,-1.221,-23.8536,-23.7562,-24.8729,-24.8042,-6.3042,-6.3074,-11.6774,-11.6958,-25.1958,
            -25.1864,-22.6864,-22.6774,-6.6622,-11.6622,-19.6621,-19.6666,-25.1666,-25.1958,-11.6958,-6.6958,-6.6917,-0.3694,6.6302,6.4701,6.4328,
            -0.5671,-11.5668,-11.6622,-19.6621,-19.6666,-25.1666,-25.1958,-25.1864,-22.6864,-22.6774,-22.5728,-25.0727,-24.9951,-29.9949,-30.1864,
            -33.6864,-33.6939,-35.6939,-35.6747,-35.6665,-31.8332,-31.8186,-29.8186,-29.7948,-27.1282,-27.0835,-24.5836,-24.5425,-24.5086,-19.5088,
            -19.4424,-17.4425,-17.4113,-11.4116,-11.3688,0.2375,15.7365,15.6755,16.1755,16.1101,15.6101,15.5619,12.5622,12.5319,18.0314,18.0435,
            22.8765,22.8431,21.5099,21.4492,17.4495,17.4329,11.9333,9.9335,9.9456,-0.0537,-4.0534,-3.9791,-10.9785,-10.8432,-3.8437,-3.7708,0.2289,
            2.5394,13.0391,13.0132,12.3466,12.3383,20.0046,20.0291,24.0289,24.0482,28.548,28.5818,36.5815,36.5953,36.6069,35.6069,35.6656,35.1656,
            35.1682,35.1913,32.7746,32.7785,32.0285,32.0293,28.696,28.6957,22.6957,22.6952,21.6952,13.1952,13.196,10.696,10.6958,8.1958,8.1622,2.6622,
            2.6076,2.3576,2.3257,3.3257,3.3044,2.5544,-15.9436,-15.8328,-11.8664,-3.8337,-3.8842,-3.9445,-0.5671,6.4328,6.3377,-0.6622,-6.6622,
            -11.6622,-11.5668};

    public static String getDSOType (String abbr) {
        String[] typeAbbrs = {"AST","BN", "C/N","DN","EG","EN","ER","GC","IG","OC","PL","PN","RN","SG","SN"};
        String[] typeNames = {"Asterism","Bright Nebula","Cluster w/Nebulosity","Dark Nebula","Elliptical Galaxy",
                "Emission Nebula","Em./Refl. Nebula","Globular Cluster",
                "Irregular Galaxy","Open Cluster","Planet","Planetary Nebula","Reflection Nebula"
                ,"Spiral Galaxy","Supernova Remnant"};
        String typeName = "";
        for (int i=0; i<typeAbbrs.length;i++) {
            if(typeAbbrs[i].equals(abbr)) {
                typeName = typeNames[i];
                break;
            }
        }
        return typeName;
    }

    // convert degrees from double format to dms string format
    public static String convertLatToDMS(double dd) {
        String latNS;
        if (dd < 0) {latNS = "S";} else {latNS = "N";}
        dd = Math.abs(dd);
        int deg = (int) dd;
        long min =  Math.round ((dd-deg)*60);
        if(min == 60) {min = 0; deg++;}
        return latNS + deg + "\u00B0 " + String.format("%02d",Math.abs(min)) + "'";
    }

    // convert degrees from double format to dms string format
    public static String convertLongToDMS(double dd) {
        String longEW;
        if (dd < 0) {longEW = "W";} else {longEW = "E";}
        dd = Math.abs(dd);
        int deg = (int) dd;
        long min =  Math.round ((dd-deg)*60);
        if(min == 60) {min = 0; deg++;}
        return longEW + deg + "\u00B0 " + String.format("%02d",Math.abs(min)) + "'";
    }

}
