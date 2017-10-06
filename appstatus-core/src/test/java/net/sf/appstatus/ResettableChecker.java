package net.sf.appstatus;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import net.sf.appstatus.core.check.AbstractCheck;
import net.sf.appstatus.core.check.CheckResultBuilder;
import net.sf.appstatus.core.check.ICheckResult;
import net.sf.appstatus.core.check.IResettableCheck;

public class ResettableChecker extends AbstractCheck implements IResettableCheck {

    public static final String NAME = "resettable";

    private AtomicBoolean error = new AtomicBoolean(true);

    @Override
    public ICheckResult checkStatus(Locale locale) {
        CheckResultBuilder result = result(this);

        if (error.get()) {
            result.code(ICheckResult.ERROR).fatal();
        } else {
            result.code(ICheckResult.OK);
        }

        return result.build();
    }

    public void reset() {
        error.set(!error.get());
    }

    public String getGroup() {
        return NAME;
    }

    public String getName() {
        return NAME;
    }

}
