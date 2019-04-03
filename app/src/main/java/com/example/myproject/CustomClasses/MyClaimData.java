package com.example.myproject.CustomClasses;

public class MyClaimData {
    private String ClaimId;
    private String ClaimDate;
    private String Description;

    public MyClaimData(String claimId, String description) {
        ClaimId = claimId;
        ClaimDate = claimId.substring(0,2)+"/"+claimId.substring(2,4)+"/"+claimId.substring(4,8);
        Description = description;
    }

    public String getClaimId() {
        return ClaimId;
    }

    public void setClaimId(String claimId) {
        ClaimId = claimId;
    }

    public String getClaimDate() {
        return ClaimDate;
    }

    public void setClaimDate(String claimDate) {
        ClaimDate = claimDate;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
