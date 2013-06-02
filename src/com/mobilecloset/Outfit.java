package com.mobilecloset;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Outfit implements Parcelable
{
  String name;
  ArrayList<Clothing> clothing;

  public Outfit(String name, ArrayList<Clothing> clothing)
  {
    super();
    this.name = name;
    this.clothing = clothing;
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeString(name);
    dest.writeList(clothing);
  }

  public static final Parcelable.Creator<Outfit> CREATOR = new Parcelable.Creator<Outfit>()
  {
    public Outfit createFromParcel(Parcel in)
    {
      return new Outfit(in);
    }

    public Outfit[] newArray(int size)
    {
      return new Outfit[size];
    }
  };

  public Outfit(Parcel in)
  {
    super();
    this.name = in.readString();
    this.clothing = in.readArrayList(Clothing.class.getClassLoader());
  }

}
