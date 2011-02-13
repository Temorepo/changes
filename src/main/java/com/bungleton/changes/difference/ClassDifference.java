package com.bungleton.changes.difference;

public class ClassDifference
{
    public final String className;

    public ClassDifference (String className)
    {
        this.className = className;
    }

    @Override
    public String toString ()
    {
        return getClass().getSimpleName() + "[" + className + "]";
    }

    @Override
    public boolean equals (Object obj)
    {
        return obj != null && obj.getClass().equals(getClass())
            && className.equals(((ClassDifference)obj).className);
    }

    @Override
    public int hashCode ()
    {
        int result = 12 * 37 + getClass().getSimpleName().hashCode();
        return result * 37 + className.hashCode();
    }
}
