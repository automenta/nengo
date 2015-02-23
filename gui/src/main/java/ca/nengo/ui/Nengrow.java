package ca.nengo.ui;


import org.simplericity.macify.eawt.Application;
import org.simplericity.macify.eawt.DefaultApplication;

public class Nengrow extends AbstractNengo {

    @Override
    protected void initialize() {
        super.initialize();

        add(getUniverse());
    }

    public static void main(String[] args) {
        Application application = new DefaultApplication();

        Nengrow ng = new Nengrow();
        ng.setApplication(application);

    }
}
