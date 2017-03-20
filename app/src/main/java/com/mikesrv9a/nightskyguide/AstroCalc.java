package com.mikesrv9a.nightskyguide;

// J2000.0 Epoch = JD 2451545.0 = 1/1/2000 12:00:00 UT
// J2010.0 Epoch = JD 2455196.5 = 12/31/2009 00:00:00 UT
// Unix/Java Epoch = JD 2440587.5 (1/1/1970 00:00:00 UT
// Sidereal formulas adapted from http://aa.usno.navy.mil/faq/docs/GAST.php
// Alt/Az formulas adapted from http://aa.usno.navy.mil/faq/docs/Alt_Az.php
// Additional info at https://en.wikipedia.org/wiki/Sidereal_time
// Additional info at http://www.stargazing.net/kepler/altaz.html

public class AstroCalc {

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
        String[] constAbbrs = {"AND","AQR","AUR","CAP","CAS","CET","CMA","CNC","COM","CVN",
                "CYG","DRA","GEM","HER","HYA","LEO","LEP","LYR","MON","OPH","ORI","PEG","PER","PSC",
                "PUP","SCO","SCT","SER","SGE","SGR","TAU","TRI","UMA","VIR","VUL"};
        String[] constNames = {"Andromeda","Aquarius","Auriga","Capricornus","Cassiopeia",
                "Cetus","Canis Major","Cancer","Coma Berenices","Canes Venatici","Cygnus","Draco",
                "Gemini","Hercules","Hydra","Leo","Lepus","Lyra","Monoceros","Ophiuchus","Orion",
                "Pegasus","Perseus","Pisces","Puppis","Scorpius","Scutum","Serpens","Sagitta",
                "Sagittarius","Taurus","Triangulum","Ursa Major","Virgo","Vulpecula"};
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
        String[] typeAbbrs = {"AST","C/N","EG","EN","ER","GC","IG","OC","PN","RN","SG","SNR"};
        String[] typeNames = {"Asterism","Cluster w/ Nebulosity","Elliptical Galaxy",
                "Emission Nebula","Emission/Reflection Nebula","Globular Cluster",
                "Irregular Galaxy","Open Cluster","Planetary Nebula","Reflection Nebula"
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

}
