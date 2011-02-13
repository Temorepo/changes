package com.bungleton.changes.difference;

public class MethodDifference extends ClassDifference
{
    public final String signature;

    public MethodDifference (String className, String signature)
    {
        super(className);
        this.signature = signature;
    }

    @Override
    public String toString ()
    {
        return getClass().getSimpleName() + "[" + className + " " + signature + "]";
    }

    @Override
    public boolean equals (Object obj)
    {
        return super.equals(obj) && signature.equals(((MethodDifference)obj).signature);
    }

    @Override
    public int hashCode ()
    {
        return super.hashCode() * 37 + signature.hashCode();
    }

}
