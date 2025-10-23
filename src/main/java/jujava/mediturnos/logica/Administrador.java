package jujava.mediturnos.logica;

public class Administrador extends Usuario{

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
            HelperCSV.guardarMedicos(gestor.medicos);}}
    //REGISTRAR ADMINISTRADOR
    public void registrarAdministrador(Administrador a){

        if(gestor.validarDNIUnico(String.valueOf(a.getDni()))){
            gestor.administradores.add(a);
            HelperCSV.guardarAdministradores(gestor.administradores);}}


    //MODIFICAR MEDICO
    public void modificarMedico(int DNI,String nombre, String apellido, int telefono,String matricula,String especialidad){

        Usuario usuario=gestor.buscarMedicoPorDNI(DNI);
        if(usuario!=null && usuario instanceof Medico ){
            Medico medico=(Medico) usuario;
            medico.setNombre(nombre);
            medico.setApellido(apellido);
            medico.setTelefono(telefono);
            medico.setMatricula(matricula);
            medico.setEspecialidad(especialidad);
            HelperCSV.guardarMedicos(gestor.medicos);}}

    //MODIFICAR ADMINISTRADOR
    public void modificarAdministrador( int DNI,String nombre, String apellido, int telefono,String area){

        Usuario usuario=gestor.buscarUsuarioPorDNI(DNI);
        if(usuario!=null && usuario instanceof Administrador ){
            Administrador administrador=(Administrador) usuario;
            administrador.setNombre(nombre);
            administrador.setApellido(apellido);
            administrador.setTelefono(telefono);
            administrador.setArea(area);
            HelperCSV.guardarAdministradores(gestor.administradores);}}



}



