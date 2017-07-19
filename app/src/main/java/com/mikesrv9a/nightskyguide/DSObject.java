package com.mikesrv9a.nightskyguide;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
    Integer dsoObserved;    // DSO Observed (not observed = 0, observed = tbd)
    Double dsoAlt;          // DSO current altitude in sky (ddd.ddd)
    Double dsoAz;           // DSO current azimuth in sky (ddd.ddd)
    Double dsoSortAlt;      // used to sort DSOs in viewing order (setting on horizon = 0 deg)
    Double dsoOnHorizCosHA; // used to calculate rise/set times for each object
    String dsoRiseTimeStr;
    String dsoSetTimeStr;


    // DSObject constructor
    DSObject (String id, String type, Double mag, String size, String dist,
                     Double ra, Double dec, String cons, String name, String psa,
                     String oith, Integer observed) {
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
        dsoObserved = observed;
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

    Integer getDsoObserved() {return dsoObserved;}

    void setDsoObserved(Integer observed) {dsoObserved = observed;}

    Double getDsoAlt() {return dsoAlt;}

    Double getDsoAz() {return dsoAz;}

    Double getDsoSortAlt() {return dsoSortAlt;}

    Double getDsoOnHorizCosHA() {return dsoOnHorizCosHA;}

    String getDsoRiseTimeStr() {return dsoRiseTimeStr;}

    String getDsoSetTimeStr() {return dsoSetTimeStr;}

    // setter methods
    public void setDsoAltAz(Double userLat, Double userLong) {
        // Temporary variables for DateTime and userLat/userLong
        // Create Joda DateTime instance and set date to desired time
        DateTime dateCal = new DateTime(DateTimeZone.UTC);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("M/d hh:mm a");

        // Calculate Alt and Az
        double daysSinceJ2000 = AstroCalc.daysSinceJ2000((dateCal.getMillis()));
        double greenwichST = AstroCalc.greenwichST(daysSinceJ2000);
        double localST = AstroCalc.localST(greenwichST, userLong);
        double hourAngle = AstroCalc.hourAngle(localST, dsoRA);
        dsoOnHorizCosHA = AstroCalc.dsoOnHorizCosHA(dsoDec, userLat);
        // calculate rise and set times - work in progress
        if (dsoOnHorizCosHA < -1) {
                dsoRiseTimeStr="Circumpolar: never";
                dsoSetTimeStr="sets below horizon";}
            else if (dsoOnHorizCosHA > 1) {
                dsoRiseTimeStr="This DSO never rises";
                dsoSetTimeStr="at this latitude";}
            else {                            // 86164.1 is number of seconds in sidereal day
                int riseOffset = (int) ((localST - dsoRA + Math.toDegrees(
                        Math.acos(dsoOnHorizCosHA))) * 86164.1/360);
                int setOffset = (int) ((-localST + dsoRA + Math.toDegrees(
                        Math.acos(dsoOnHorizCosHA))) * 86164.1/360);
                if (setOffset < 0) {   // change to next day - accounts for time zone
                    riseOffset = riseOffset - 86164;
                    setOffset = setOffset + 86164;
                }
                dsoRiseTimeStr = dateCal.minusSeconds(riseOffset).
                        withZone(DateTimeZone.getDefault()).toString(dtf);
                dsoSetTimeStr = dateCal.plusSeconds(setOffset).
                        withZone(DateTimeZone.getDefault()).toString(dtf);
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
        parcel.writeInt(dsoObserved);
        parcel.writeDouble(dsoAlt);
        parcel.writeDouble(dsoAz);
        parcel.writeDouble(dsoOnHorizCosHA);
        parcel.writeString(dsoRiseTimeStr);
        parcel.writeString(dsoSetTimeStr);
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
        dsoRiseTimeStr = in.readString();
        dsoSetTimeStr = in.readString();
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
