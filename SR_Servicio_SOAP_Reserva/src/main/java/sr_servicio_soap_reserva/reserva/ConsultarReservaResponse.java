//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.07.02 a las 08:17:04 PM PET 
//


package sr_servicio_soap_reserva.reserva;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codReserva" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cliente" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="habitacion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="piso" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="fechaEntrada" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="fechaSalida" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="precioPorDia" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="dias" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="total" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "codReserva",
    "cliente",
    "habitacion",
    "tipo",
    "piso",
    "fechaEntrada",
    "fechaSalida",
    "precioPorDia",
    "dias",
    "total"
})
@XmlRootElement(name = "ConsultarReservaResponse")
public class ConsultarReservaResponse {

    @XmlElement(required = true)
    protected String codReserva;
    @XmlElement(required = true)
    protected String cliente;
    @XmlElement(required = true)
    protected String habitacion;
    @XmlElement(required = true)
    protected String tipo;
    @XmlElement(required = true)
    protected String piso;
    @XmlElement(required = true)
    protected String fechaEntrada;
    @XmlElement(required = true)
    protected String fechaSalida;
    @XmlElement(required = true)
    protected BigDecimal precioPorDia;
    protected int dias;
    @XmlElement(required = true)
    protected BigDecimal total;

    /**
     * Obtiene el valor de la propiedad codReserva.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodReserva() {
        return codReserva;
    }

    /**
     * Define el valor de la propiedad codReserva.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodReserva(String value) {
        this.codReserva = value;
    }

    /**
     * Obtiene el valor de la propiedad cliente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * Define el valor de la propiedad cliente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCliente(String value) {
        this.cliente = value;
    }

    /**
     * Obtiene el valor de la propiedad habitacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHabitacion() {
        return habitacion;
    }

    /**
     * Define el valor de la propiedad habitacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHabitacion(String value) {
        this.habitacion = value;
    }

    /**
     * Obtiene el valor de la propiedad tipo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Define el valor de la propiedad tipo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipo(String value) {
        this.tipo = value;
    }

    /**
     * Obtiene el valor de la propiedad piso.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPiso() {
        return piso;
    }

    /**
     * Define el valor de la propiedad piso.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPiso(String value) {
        this.piso = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaEntrada.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaEntrada() {
        return fechaEntrada;
    }

    /**
     * Define el valor de la propiedad fechaEntrada.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaEntrada(String value) {
        this.fechaEntrada = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaSalida.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaSalida() {
        return fechaSalida;
    }

    /**
     * Define el valor de la propiedad fechaSalida.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaSalida(String value) {
        this.fechaSalida = value;
    }

    /**
     * Obtiene el valor de la propiedad precioPorDia.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPrecioPorDia() {
        return precioPorDia;
    }

    /**
     * Define el valor de la propiedad precioPorDia.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPrecioPorDia(BigDecimal value) {
        this.precioPorDia = value;
    }

    /**
     * Obtiene el valor de la propiedad dias.
     * 
     */
    public int getDias() {
        return dias;
    }

    /**
     * Define el valor de la propiedad dias.
     * 
     */
    public void setDias(int value) {
        this.dias = value;
    }

    /**
     * Obtiene el valor de la propiedad total.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Define el valor de la propiedad total.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotal(BigDecimal value) {
        this.total = value;
    }

}
