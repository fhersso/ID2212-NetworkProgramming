/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter.model;

import java.io.Serializable;
import java.util.HashMap;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Fhersso
 */
@Entity
public class Converter implements ConverterDTO, Serializable {

    private HashMap<String, Double> rates;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    public Converter() {
    }

    public Converter(double usd, double eur, double gbp, double yen) {
        rates = new HashMap<>();
        rates.put("usd", usd);
        rates.put("eur", eur);
        rates.put("gbp", gbp);
        rates.put("yen", yen);
    }

    @Override
    public double convertRate(String source, String target) {
        double rateToTarget = rates.get(target);
        double rateToDollars = rates.get(source);
        return (rateToTarget / rateToDollars) ;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Converter)) {
            return false;
        }
        Converter other = (Converter) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "converter.model.Converter[ id=" + id + " ]";
    }

}
