package net.sf.appstatus;

import static org.junit.Assert.assertTrue;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.check.ICheck;
import net.sf.appstatus.core.check.ICheckResult;
import net.sf.appstatus.core.check.IResettableCheck;

public class ReseattableCheckerTest extends AppStatus {

    @Before
    public void setUp() {
        this.init();
    }

    @After
    public void tearDown() {
        this.close();
    }

    @Test
    public void testReset() throws Exception {
        IResettableCheck check = (IResettableCheck) CollectionUtils.find(checkers, new Predicate() {
            public boolean evaluate(Object check) {
                return StringUtils.equals(((ICheck) check).getName(), ResettableChecker.NAME);
            }
        });

        assertTrue(check.checkStatus(null).getCode() == ICheckResult.ERROR);

        check.reset();

        assertTrue(check.checkStatus(null).getCode() == ICheckResult.OK);

    }
}
