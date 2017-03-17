package com.mikesrv9a.nightskyguide;

import android.os.Parcel;
import android.os.Parcelable;
import android.renderscript.Double2;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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

    Double getDsoAlt() {return dsoAlt;}

    Double getDsoAz() {return dsoAz;}

    Double getDsoSortAlt() {return dsoSortAlt;}

    // setter methods
    public void setDsoAltAz() {
        // Temporary variables for DateTime and userLat/userLong
        // Create Joda DateTime instance and set date to desired time
        DateTime dateCal = new DateTime(DateTimeZone.UTC);
        // set user location
        double userLat = 45 + (13 + 59.88/60)/60;
        double userLong = -93 + (17 + 28.84/60)/60;

        // Calculate Alt and Az
        double daysSinceJ2000 = AstroCalc.daysSinceJ2000((dateCal.getMillis()));
        double greenwichST = AstroCalc.greenwichST(daysSinceJ2000);
        double localST = AstroCalc.localST(greenwichST, userLong);
        double hourAngle = AstroCalc.hourAngle(localST, dsoRA);
        dsoAlt = AstroCalc.dsoAlt(dsoDec, userLat, hourAngle);
        dsoAz = AstroCalc.dsoAz(dsoDec, userLat, hourAngle, dsoAlt);
        if (dsoAz >= 180) {
            if (dsoAlt >=1) {dsoSortAlt = dsoAlt;}   // if alt<1° then consider set
            else {dsoSortAlt = 360 + dsoAlt;}}
        else {dsoSortAlt = 180 - dsoAlt;}
    }

    public void setDsoObserved(Integer observed) {dsoObserved = observed;}

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
