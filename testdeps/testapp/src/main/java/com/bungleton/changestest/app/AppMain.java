package com.bungleton.changestest.app;

import com.bungleton.changestest.comp.ThatOtherThingUser;
import com.bungleton.changestest.lib.ThatThingAddedInV2;

public class AppMain
{
    public AppMain ()
    {
        new ThatOtherThingUser().useIt();
        new ThatThingAddedInV2().doThatThing();
    }

    public static void main (String [] args)
    {
        new AppMain();
    }
}
