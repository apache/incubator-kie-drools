package org.acme;

import java.util.List;

public class LoanAppDTO {
	   private int maxAmount;
	   private List<LoanApplication> loanApplications;

	   public int getMaxAmount() {
	       return maxAmount;
	   }

	   public void setMaxAmount(int maxAmount) {
	       this.maxAmount = maxAmount;
	   }

	   public List<LoanApplication> getLoanApplications() {
	       return loanApplications;
	   }

	   public void setLoanApplications(List<LoanApplication> loanApplications) {
	       this.loanApplications = loanApplications;
	   }
	}