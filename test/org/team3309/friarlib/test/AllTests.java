package org.team3309.friarlib.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.team3309.friarlib.test.filter.MaxChangeFilterTest;

@RunWith(Suite.class)
@SuiteClasses({ TankDriveTest.class, MaxChangeFilterTest.class })
public class AllTests {

}
