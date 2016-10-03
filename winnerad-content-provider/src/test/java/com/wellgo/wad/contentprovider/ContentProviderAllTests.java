package com.wellgo.wad.contentprovider;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.wellgo.wad.contentprovider.suit.ContentProviderVerticleTest;





// specify a runner class: Suite.class
@RunWith(Suite.class)

// specify an array of test classes
@Suite.SuiteClasses({
  ContentProviderVerticleTest.class, 
  // ContentProviderRemoteVerticleTest.class
  }
)


public class ContentProviderAllTests {

}
