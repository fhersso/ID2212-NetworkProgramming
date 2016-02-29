/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter.controller;

import converter.model.Converter;
import converter.model.ConverterDTO;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Fhersso
 */
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class ConvertFacade {

    @PersistenceContext(unitName = "converterPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public Integer createConverter(double usd, double eur, double gbp, double yen) {
        Converter newConv = new Converter(usd, eur, gbp, yen);
        em.persist(newConv);
        return newConv.getId();
    }

    public ConverterDTO findConverter(int acctNo) {
        ConverterDTO found = em.find(Converter.class, acctNo);
        if (found == null) {
            throw new EntityNotFoundException("No Converter with number " + acctNo);
        }
        return found;
    }


}
