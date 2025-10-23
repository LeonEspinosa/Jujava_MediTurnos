package jujava.mediturnos.logica.entidades;

import jujava.mediturnos.datos.AccesoDatos;
import jujava.mediturnos.logica.GestorUsuario;

public class Administrador extends Persona {

    String area;
    public Administrador() {}

    public Administrador(String nombre, String apellido, int dni, char genero, int telefono,String area ) {
        super(nombre, apellido, dni, genero, telefono);
        this.area = area;}
    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area = area;
    }
    @Override
    public String toString() {
        return "administrador " +
                "nombre:" + getNombre() +
                ", apellido:" + getApellido()+
                ", dni:" + getDni() +
                ", genero:" + getGenero() +
                ", telefono:" + getTelefono() +
                ", area:" + getArea();
    }
    private GestorUsuario gestor=new GestorUsuario();
    //REGISTRAR MEDICO
    public void registrarMedico(Medico m){
        if(gestor.validarDNIUnico(String.valueOf(m.getDni()))){
            gestor.medicos.add(m);
            AccesoDatos.guardarMedicos(gestor.medicos);}}
    //REGISTRAR ADMINISTRADOR
    public void registrarAdministrador(Administrador a){

        if(gestor.validarDNIUnico(String.valueOf(a.getDni()))){
            gestor.administradores.add(a);
            AccesoDatos.guardarAdministradores(gestor.administradores);}}


    //MODIFICAR MEDICO
    public void modificarMedico(int DNI,String nombre, String apellido, int telefono,String matricula,String especialidad){

        Persona persona =gestor.buscarMedicoPorDNI(DNI);
        if(persona !=null && persona instanceof Medico ){
            Medico medico=(Medico) persona;
            medico.setNombre(nombre);
            medico.setApellido(apellido);
            medico.setTelefono(telefono);
            medico.setMatricula(matricula);
            medico.setEspecialidad(especialidad);
            AccesoDatos.guardarMedicos(gestor.medicos);}}

    //MODIFICAR ADMINISTRADOR
    public void modificarAdministrador( int DNI,String nombre, String apellido, int telefono,String area){

        Persona persona =gestor.buscarUsuarioPorDNI(DNI);
        if(persona !=null && persona instanceof Administrador ){
            Administrador administrador=(Administrador) persona;
            administrador.setNombre(nombre);
            administrador.setApellido(apellido);
            administrador.setTelefono(telefono);
            administrador.setArea(area);
            AccesoDatos.guardarAdministradores(gestor.administradores);}}



}



