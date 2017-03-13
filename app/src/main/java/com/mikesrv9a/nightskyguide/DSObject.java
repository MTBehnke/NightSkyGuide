package com.mikesrv9a.nightskyguide;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.renderscript.Double2;
import android.support.v7.app.AppCompatActivity;

/**
 * Creates arraylist of DSObjects and updates sky position, etc.
 */

public class DSObject implements Parcelable {

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


    // DSObject constructor
    public DSObject (String id, String type, Double mag, String size, String dist,
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
    }

    // getter methods
    public String getDsoObjectID() {return dsoObjectID;}

    public String getDsoType() {return dsoType;}

    public Double getDsoMag() {return dsoMag;}

    public String getDsoSize()  {return dsoSize;}

    public String getDsoDist() {return dsoDist;}

    public Double getDsoRA() {return dsoRA;}

    public Double getDsoDec() {return dsoDec;}

    public String getDsoConst() {return dsoConst;}

    public String getDsoName() {return dsoName;}

    public String getDsoPSA() {return dsoPSA;}

    public String getDsoOITH() {return dsoOITH;}

    public Integer getDsoObserved() {return dsoObserved;}

    public Double getDsoAlt() {return dsoAlt;}

    public Double getDsoAz() {return dsoAz;}

    // setter methods
    public void setDsoAlt(Double alt) {dsoAlt = alt;}

    public void setDsoAz(Double az) {dsoAz = az;}

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
}
