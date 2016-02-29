/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter.view;

import converter.controller.ConvertFacade;
import converter.model.ConverterDTO;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Fhersso
 */
@Named("converterManager")
@ConversationScoped
public class ConverterManager implements Serializable {

    private static final long serialVersionUID = 16247164405L;
    @EJB
    private ConvertFacade convertFacade;
    private ConverterDTO currentConvert;
    private Integer currentId;
    private String newSource;
    private String newTarget;
    private double newSourceAmount;
    private double newResultAmount;
    private double rateUSD;
    private double rateEUR;
    private double rateGBP;
    private double rateYEN;
    private Exception transactionFailure;
    @Inject
    private Conversation conversation;

    private void startConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    private void stopConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    private void handleException(Exception e) {
        stopConversation();
        e.printStackTrace(System.err);
        transactionFailure = e;
    }

    /**
     * @return <code>true</code> if the latest transaction succeeded, otherwise
     * <code>false</code>.
     */
    public boolean getSuccess() {
        return transactionFailure == null;
    }

    /**
     * Returns the latest thrown exception.
     *
     * @return
     */
    public Exception getException() {
        return transactionFailure;
    }

    /**
     * This return value is needed because of a JSF 2.2 bug. Note 3 on page 7-10
     * of the JSF 2.2 specification states that action handling methods may be
     * void. In JSF 2.2, however, a void action handling method plus an
     * if-element that evaluates to true in the faces-config navigation case
     * causes an exception.
     *
     * @return an empty string.
     */
    private String jsf22Bugfix() {
        return "";
    }

    public String saveRates() {
        try {
            startConversation();
            transactionFailure = null;
            currentId = convertFacade.createConverter(rateUSD, rateEUR, rateGBP, rateYEN);
            //newResultAmount = newSourceAmount * currentConvert.convertRate(newSource, newTarget);
        } catch (Exception e) {
            handleException(e);
        }
        return jsf22Bugfix();
    }

    public String convertCurrency() {
        try {
            startConversation();
            transactionFailure = null;
            currentConvert = convertFacade.findConverter(currentId);
            newResultAmount = newSourceAmount * currentConvert.convertRate(newSource, newTarget);
        } catch (Exception e) {
            handleException(e);
        }
        return jsf22Bugfix();
    }

    public void setNewSource(String newSource) {
        this.newSource = newSource;
    }

    public void setNewTarget(String newTarget) {
        this.newTarget = newTarget;
    }

    public void setNewSourceAmount(double newSourceAmount) {
        this.newSourceAmount = newSourceAmount;
    }

    public void setNewResultAmount(double newResultAmount) {
        this.newResultAmount = newResultAmount;
    }

    public String getNewSource() {
        return null;
    }

    public String getNewTarget() {
        return null;
    }

    public double getNewSourceAmount() {
        return this.newSourceAmount;
    }

    public double getNewResultAmount() {
        return this.newResultAmount;
    }

    public void setRateUSD(double rateUSD) {
        this.rateUSD = rateUSD;
    }

    public void setRateEUR(double rateEUR) {
        this.rateEUR = rateEUR;
    }

    public void setRateGBP(double rateGBP) {
        this.rateGBP = rateGBP;
    }

    public void setRateYEN(double rateYEN) {
        this.rateYEN = rateYEN;
    }

    public double getRateUSD() {
        return rateUSD;
    }

    public double getRateEUR() {
        return rateEUR;
    }

    public double getRateGBP() {
        return rateGBP;
    }

    public double getRateYEN() {
        return rateYEN;
    }
}
