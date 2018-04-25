package com.mikesrv9a.nightskyguide;

import android.os.Parcel;
import android.os.Parcelable;

class Observation implements Parcelable {

    Integer obsDBid;
    String obsDsoID;
    String obsDate;
    String obsLocation;
    String obsSeeing;
    String obsTransparency;
    String obsTelescope;
    String obsEyepiece;
    String obsPower;
    String obsFilter;
    String obsNotes;
    String obsCatalogue;
    String obsProgram;

    // constructor
    Observation (Integer dbID, String id, String date, String location, String seeing, String transparency, String telescope,
                 String eyepiece, String power, String filter, String notes, String catalogue, String program) {
        obsDBid = dbID;
        obsDsoID = id;
        obsDate = date;
        obsLocation = location;
        obsSeeing = seeing;
        obsTransparency = transparency;
        obsTelescope = telescope;
        obsEyepiece = eyepiece;
        obsPower = power;
        obsFilter = filter;
        obsNotes = notes;
        obsCatalogue = catalogue;
        obsProgram = program;
    }

    // getter methods
    Integer getObsDBid() {return obsDBid;}
    String getObsDsoID() {return obsDsoID;}
    String getObsDate() {return obsDate;}
    String getObsLocation() {return obsLocation;}
    String getObsSeeing() {return obsSeeing;}
    String getObsTransparency() {return obsTransparency;}
    String getObsTelescope() {return obsTelescope;}
    String getObsEyepiece() {return obsEyepiece;}
    String getObsPower() {return obsPower;}
    String getObsFilter() {return obsFilter;}
    String getObsNotes() {return obsNotes;}
    String getObsCatalogue() {return obsCatalogue;}
    String getObsProgram() {return obsProgram;}

    // Required methods for parcelable

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(obsDBid);
        parcel.writeString(obsDsoID);
        parcel.writeString(obsDate);
        parcel.writeString(obsLocation);
        parcel.writeString(obsSeeing);
        parcel.writeString(obsTransparency);
        parcel.writeString(obsTelescope);
        parcel.writeString(obsEyepiece);
        parcel.writeString(obsPower);
        parcel.writeString(obsFilter);
        parcel.writeString(obsNotes);
        parcel.writeString(obsCatalogue);
        parcel.writeString(obsProgram);
    }

    // required method, not used
    private Observation(Parcel in) {
        obsDBid = in.readInt();
        obsDsoID = in.readString();
        obsDate = in.readString();
        obsLocation = in.readString();
        obsSeeing = in.readString();
        obsTransparency = in.readString();
        obsTelescope = in.readString();
        obsEyepiece = in.readString();
        obsPower = in.readString();
        obsFilter = in.readString();
        obsNotes = in.readString();
        obsCatalogue = in.readString();
        obsProgram = in.readString();
    }

    // required method, not used
    public static final Creator<Observation> CREATOR = new Creator<Observation>() {
        @Override
        public Observation createFromParcel(Parcel in) {
            return new Observation(in);
        }

        @Override
        public Observation[] newArray(int size) {
            return new Observation[size];
        }
    };

}
