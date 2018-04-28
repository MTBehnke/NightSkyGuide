package com.mikesrv9a.nightskyguide;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Arrays;

/** Creates DSObjects and updates sky position, etc.
 */

class DSObject implements Parcelable {

    String dsoObjectID;     // DSO Object ID (e.g. M31)
    String dsoType;         // DSO Type (e.g. SG = Spiral Galaxy)
    Double dsoMag;          // DSO Magnitude
    String dsoSize;         // DSO Size (arc-mins)
    String dsoDist;         // DSO Distance (ly, Kly, Mly)
    Double dsoRA;           // DSO Right Ascension (ddd.ddd)
    Double dsoDec;          // DSO Declination (ddd.ddd)
    String dsoConst;        // DSO Constellation (e.g. AND)
    String dsoName;         // DSO Common Name (e.g. Andromeda Galaxy)
    String dsoPSA;          // DSO Page Number(s) in S&T Pocket Sky Atlas
    String dsoOITH;         // DSO Page Number(s) in Objects in the Heavens
    String dsoSkyAtlas;     // DSO Page Number(s) for Sky Atlas 2000
    String dsoTurnLeft;     // DSO Page Number for Turn Left At Orion
    String dsoCatalogue;    // DSO Catalogue ID (e.g. NGC #)
    String dsoObsProgram;   // DSO Observation Program (Messier "M###", Caldwell "C###", Hershel 400 "H400")
    Integer dsoObserved;    // DSO Observed (not observed = 0, observed = tbd)
    Double dsoAlt;          // DSO current altitude in sky (ddd.ddd)
    Double dsoAz;           // DSO current azimuth in sky (ddd.ddd)
    Double dsoSortAlt;      // used to sort DSOs in viewing order (setting on horizon = 0 deg)
    Double dsoOnHorizCosHA; // used to calculate rise/set times for each object
    DateTime dsoRiseTime;
    DateTime dsoSetTime;
    DateTime dsoTransitTime;


    // DSObject constructor
    DSObject (String id, String type, Double mag, String size, String dist,
                     Double ra, Double dec, String cons, String name, String psa,
                     String oith, String skyatlas, String turnleft, String catalogue, String obsProgram ,Integer observed) {
        dsoObjectID = id;
        dsoType = type;
        dsoMag = mag;
        dsoSize = size;
        dsoDist = dist;
        dsoRA = ra;
        dsoDec = dec;
        dsoConst = cons;
        dsoName = name;
        dsoPSA = psa;
        dsoOITH = oith;
        dsoSkyAtlas = skyatlas;
        dsoTurnLeft = turnleft;
        dsoCatalogue = catalogue;
        dsoObserved = observed;
        dsoObsProgram = obsProgram;
        dsoAlt = 0.0;
        dsoAz = 0.0;
        dsoSortAlt = 0.0;
        dsoOnHorizCosHA = 0.0;
    }

    // getter methods
    String getDsoObjectID() {return dsoObjectID;}

    String getDsoType() {return dsoType;}

    Double getDsoMag() {return dsoMag;}

    String getDsoSize()  {return dsoSize;}

    String getDsoDist() {return dsoDist;}

    Double getDsoRA() {return dsoRA;}

    Double getDsoDec() {return dsoDec;}

    String getDsoConst() {return dsoConst;}

    String getDsoName() {return dsoName;}

    String getDsoPSA() {return dsoPSA;}

    String getDsoOITH() {return dsoOITH;}

    String getDsoSkyAtlas() {return dsoSkyAtlas;}

    String getDsoTurnLeft() {return dsoTurnLeft;}

    String getDsoCatalogue()  {return dsoCatalogue;}

    String getDsoObsProgram() {return dsoObsProgram;}

    Integer getDsoObserved() {return dsoObserved;}

    void setDsoObserved(Integer observed) {dsoObserved = observed;}

    Double getDsoAlt() {return dsoAlt;}

    Double getDsoAz() {return dsoAz;}

    Double getDsoSortAlt() {return dsoSortAlt;}

    Double getDsoOnHorizCosHA() {return dsoOnHorizCosHA;}

    DateTime getDsoRiseTime() {return dsoRiseTime;}

    DateTime getDsoSetTime() {return dsoSetTime;}

    DateTime getDsoTransitTime() {return dsoTransitTime;}

    Integer getObjectIdSort() {
        Integer sort;
        if (dsoType.equals("PL")) {sort = Arrays.asList(AstroCalc.planetName).indexOf("dsoObjectID");}    // planets first
        //else if (dsoObjectID.startsWith("M")) {sort = 100 + Integer.valueOf(dsoObjectID.substring(1));}   // Messier objects second
        //else if (dsoObjectID.startsWith("C")) {sort = 300 + Integer.valueOf(dsoObjectID.substring(1));}   // Caldwell objects third
        else {sort = 1000;}   // error handling
        return sort;
    }

    // setter methods
    public void setPlanetCoords(int planet) {
        DateTime dateCal = new DateTime(DateTimeZone.UTC);
        double daysSinceJ2010 = AstroCalc.daysSinceJ2010(dateCal.getMillis());
        double daysSinceJ2000 = daysSinceJ2010 + 3651.5;
        double obliqEclip = AstroCalc.obliqEclip(daysSinceJ2000);
        // calculate Earth coordinates
        double meanAnomoly = AstroCalc.meanAnomoly(0, daysSinceJ2010);
        double trueAnomoly = AstroCalc.trueAnomoly(0,meanAnomoly);
        double earthHelioLong = AstroCalc.helioLong(0,trueAnomoly);
        double earthRadiusVector = AstroCalc.radiusVector(0,trueAnomoly);
        // calculate planet coordinates
        meanAnomoly = AstroCalc.meanAnomoly(planet, daysSinceJ2010);
        trueAnomoly = AstroCalc.trueAnomoly(planet, meanAnomoly);
        double helioLong = AstroCalc.helioLong(planet, trueAnomoly);
        double radiusVector = AstroCalc.radiusVector(planet, trueAnomoly);
        double helioLat = AstroCalc.helioLat(planet,helioLong);
        double projHelioLong = AstroCalc.projHelioLong(planet,helioLong);
        double projRadiusVector = AstroCalc.projRadiusVector(radiusVector, helioLat);
        double geoEclLong = AstroCalc.geoEclLong(planet,projHelioLong,projRadiusVector,earthHelioLong,earthRadiusVector);
        double geoEclLat = AstroCalc.geoEclLat(projHelioLong,projRadiusVector,earthHelioLong,earthRadiusVector,geoEclLong,helioLat);
        double distPlanet = AstroCalc.distPlanet(projHelioLong, projRadiusVector,earthHelioLong,earthRadiusVector,helioLat);
        double sizePlanet = AstroCalc.sizePlanet(planet,distPlanet);
        double magPlanet = AstroCalc.magPlanet(planet,geoEclLong,helioLong,radiusVector,distPlanet);
        dsoDist = String.format("%.1f",distPlanet) + " AU";
        dsoSize = String.format("%.1f",sizePlanet) + "\"";
        magPlanet = magPlanet*10;  // these three lines round magPlanet to one decimal point
        magPlanet = (double)((int)magPlanet);
        magPlanet = magPlanet/10;
        dsoMag = magPlanet;
        dsoRA = AstroCalc.raFromEclip(obliqEclip,geoEclLong,geoEclLat);
        dsoDec = AstroCalc.decFromEclip(obliqEclip,geoEclLong,geoEclLat);
        dsoConst = AstroCalc.planetConst(dsoRA,dsoDec);
    }

    public void setDsoAltAz(Double userLat, Double userLong) {
        // Temporary variables for DateTime and userLat/userLong
        // Create Joda DateTime instance and set date to desired time
        DateTime dateCal = new DateTime(DateTimeZone.UTC);

        // Calculate Alt and Az
        double daysSinceJ2000 = AstroCalc.daysSinceJ2000((dateCal.getMillis()));
        double greenwichST = AstroCalc.greenwichST(daysSinceJ2000);
        double localST = AstroCalc.localST(greenwichST, userLong);
        double hourAngle = AstroCalc.hourAngle(localST, dsoRA);
        dsoOnHorizCosHA = AstroCalc.dsoOnHorizCosHA(dsoDec, userLat);

        /* calculate rise, transit and set times
         * Sets dsoRiseTime = null if DSO never rises ; dsoSetTime = null if DSO never sets
         * 86164.1 is number of seconds in sidereal day
         */
        int riseOffset = (int) ((localST - dsoRA + Math.toDegrees(
                Math.acos(dsoOnHorizCosHA))) * 86164.1/360);
        int setOffset = (int) ((-localST + dsoRA + Math.toDegrees(
                Math.acos(dsoOnHorizCosHA))) * 86164.1/360);
        int transitOffset = (int) ((localST - dsoRA) * 86164.1/360);
        if (setOffset < 0) {   // change to next day - accounts for time zone
            riseOffset = riseOffset - 86164;
            setOffset = setOffset + 86164;
            transitOffset = transitOffset - 86164;
        }
        if (dsoOnHorizCosHA < -1) {
                // This DSO never sets (circumpolar at user's latitude)
                dsoRiseTime = DateTime.parse("0");
                dsoSetTime = null;
                dsoTransitTime = dateCal.minusSeconds(transitOffset);
            }
            else if (dsoOnHorizCosHA > 1) {
                // This DSO never rises above horizon (at user's latitude)
                dsoRiseTime = null;
                dsoSetTime = DateTime.parse("0");
                dsoTransitTime = null;
            }
            else {
                dsoRiseTime = dateCal.minusSeconds(riseOffset);
                dsoSetTime = dateCal.plusSeconds(setOffset);
                dsoTransitTime = dateCal.minusSeconds(transitOffset);
            }
        dsoAlt = AstroCalc.dsoAlt(dsoDec, userLat, hourAngle);
        dsoAz = AstroCalc.dsoAz(dsoDec, userLat, hourAngle, dsoAlt);

        // determine sort order of objects based on altitude
        if (dsoAz >= 180) {
            if (dsoAlt >=0) {dsoSortAlt = dsoAlt;}
            else {dsoSortAlt = 360 + dsoAlt;}}
        else {dsoSortAlt = 180 - dsoAlt;}
    }

    //public void setDsoObserved(Integer observed) {dsoObserved = observed;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(dsoObjectID);
        parcel.writeString(dsoType);
        parcel.writeDouble(dsoMag);
        parcel.writeString(dsoSize);
        parcel.writeString(dsoDist);
        parcel.writeDouble(dsoRA);
        parcel.writeDouble(dsoDec);
        parcel.writeString(dsoConst);
        parcel.writeString(dsoName);
        parcel.writeString(dsoPSA);
        parcel.writeString(dsoOITH);
        parcel.writeString(dsoSkyAtlas);
        parcel.writeString(dsoTurnLeft);
        parcel.writeString(dsoCatalogue);
        parcel.writeString(dsoObsProgram);
        parcel.writeInt(dsoObserved);
        parcel.writeDouble(dsoAlt);
        parcel.writeDouble(dsoAz);
        parcel.writeDouble(dsoOnHorizCosHA);
        DateTimeFormatter dtf = DateTimeFormat.fullDateTime();
        String dsoRiseTimeStr = (dsoRiseTime == null) ? "" : dsoRiseTime.toString(dtf);
        String dsoSetTimeStr = (dsoSetTime == null) ? "" : dsoSetTime.toString(dtf);
        String dsoTransitTimeStr = (dsoTransitTime == null) ? "" : dsoTransitTime.toString(dtf);
        parcel.writeString(dsoRiseTimeStr);
        parcel.writeString(dsoSetTimeStr);
        parcel.writeString(dsoTransitTimeStr);
    }

    // required method, not used
    private DSObject(Parcel in) {
        dsoObjectID = in.readString();
        dsoType = in.readString();
        dsoSize = in.readString();
        dsoDist = in.readString();
        dsoConst = in.readString();
        dsoName = in.readString();
        dsoPSA = in.readString();
        dsoOITH = in.readString();
        dsoSkyAtlas = in.readString();
        dsoTurnLeft = in.readString();
        dsoCatalogue = in.readString();
        dsoObsProgram = in.readString();

        DateTimeFormatter dtf = DateTimeFormat.fullDateTime();
        String dsoRiseTimeStr = in.readString();
        String dsoSetTimeStr = in.readString();
        String dsoTransitTimeStr = in.readString();
        dsoRiseTime = (dsoRiseTimeStr == "") ? null : dtf.parseDateTime(in.readString());
        dsoSetTime = (dsoSetTimeStr == "") ? null : dtf.parseDateTime(in.readString());
        dsoTransitTime = (dsoTransitTimeStr == "") ? null : dtf.parseDateTime(in.readString());
    }

    // required method, not used
    public static final Creator<DSObject> CREATOR = new Creator<DSObject>() {
        @Override
        public DSObject createFromParcel(Parcel in) {
            return new DSObject(in);
        }

        @Override
        public DSObject[] newArray(int size) {
            return new DSObject[size];
        }
    };
}
