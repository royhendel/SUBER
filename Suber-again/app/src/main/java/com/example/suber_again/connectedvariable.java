package com.example.suber_again;

public class connectedvariable {
    private boolean boo = false;
    private boolean nul = false;
    private ChangeListener listener;
    private ChangeListener listener2;

    public boolean isconnected() {
        return boo;
    }

    public boolean isnul() {
        return nul;
    }
    public void setNul(Boolean n){
        this.nul = n;
    }

    public void setBoo(boolean boo) {
        this.boo = boo;
        if (listener != null) listener.onChange();
        if (listener2 != null) listener2.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public ChangeListener getListener2(){return listener2;}

    public void setListener2(ChangeListener listener){this.listener2 = listener;}

    public interface ChangeListener {
        void onChange();
    }
}
