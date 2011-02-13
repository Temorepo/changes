package com.bungleton.changes.difference;

import com.google.common.base.Preconditions;

public class MethodDifference extends ClassDifference
{
    public final String signature;

    public MethodDifference (String className, String signature)
    {
        super(className);
        Preconditions.checkNotNull(signature, "signature must not be null");
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
