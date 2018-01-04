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


import android.util.Log;

import java.util.concurrent.RecursiveTask;

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
