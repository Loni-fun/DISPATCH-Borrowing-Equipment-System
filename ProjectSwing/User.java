package ProjectSwing;


public abstract class User implements Account {
    
    protected int idNo;
    protected String lName = "";
    protected String fName = "";
    protected String mName = "";
    protected String password = "";
    protected int failedTries = 0;
    protected boolean mustChange = false;

    
    @Override
    public int getIdNumber() {
        return idNo;
    }
    
    @Override
    public boolean passwordMatches(String typed) {
        return password.equals(typed);
    }
    
    @Override
    public int getFailedTries() {
        return failedTries;
    }
    
    @Override
    public boolean isMustChange() {
        return mustChange;
    }
    
    public String getLastName() {
        return lName;
    }

    public String getFirstName() {
        return fName;
    }

    public String getMiddleName() {
        return mName;
    }

    @Override
    public String getFullName() {
        if (lName.isEmpty()) {
            return "";
        }
        return lName + ", " + fName;
    }

    @Override
    public boolean hasAccount() {
        return !password.isEmpty();
    }

  

    @Override
    public void setPassword(String newPass, boolean mustChange) {
        this.password = newPass;
        this.mustChange = mustChange;
        this.failedTries = 0;
    }

    public void setNames(String last, String first, String middle) {
        this.lName = last;
        this.fName = first;
        this.mName = middle;
    }
    
    @Override
    public void recordFailedLogin() {
        failedTries++;
        if (failedTries >= BlockedUsers.MAX_TRIES) {
            BlockedUsers.block(String.valueOf(idNo), getRole(), "Exceeded " + BlockedUsers.MAX_TRIES + " login attempts!");
        }
        save();
    }

    @Override
    public void resetTries() {
        if (failedTries != 0) {
            failedTries = 0;
            save();
        }
    }

    @Override
    public boolean isBlocked() {
        return BlockedUsers.isBlocked(String.valueOf(idNo), getRole());
    }

    @Override
    public void clearAccount() {
        lName = "";
        fName = "";
        mName = "";
        password = "";
        mustChange = false;
        failedTries = 0;
    }

}