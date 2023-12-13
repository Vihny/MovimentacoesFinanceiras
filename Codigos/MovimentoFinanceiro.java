import java.sql.Date;

public class MovimentoFinanceiro {
    private static int contadorCodigo = 1;

    private int codigo;
    private String tipo;
    private String descricao;
    private double valor;
    private Date data;

    public MovimentoFinanceiro(String tipo, String descricao, double valor, Date data) {
        this.codigo = contadorCodigo++;
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getValor() {
        return valor;
    }

    public Date getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("Código: %d, Tipo: %s, Descrição: %s, Valor: %.2f, Data: %s",
                codigo, tipo, descricao, valor, data);
    }
}
