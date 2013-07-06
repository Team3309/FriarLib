package org.team3309.friarlib.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.team3309.friarlib.test.filter.MaxChangeFilterTest;
import org.team3309.friarlib.test.util.LatchTest;

@RunWith(Suite.class)
@SuiteClasses({ TankDriveTest.class, MaxChangeFilterTest.class, LatchTest.class })
public class AllTests {

}
