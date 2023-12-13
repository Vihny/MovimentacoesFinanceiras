import java.io.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ContaCorrente {
    private List<MovimentoFinanceiro> movimentos = new ArrayList<>();

    public void incluirMovimento(String tipo, String descricao, double valor, Date data) {
        MovimentoFinanceiro novoMovimento = new MovimentoFinanceiro(tipo, descricao, valor, data);
        movimentos.add(novoMovimento);
    }

    public void estornarMovimento(int codigo) {
        for (int i = 0; i < movimentos.size(); i++) {
            MovimentoFinanceiro movimento = movimentos.get(i);
            if (movimento.getCodigo() == codigo) {
                String tipoEstorno = movimento.getTipo().equals("Débito") ? "Crédito" : "Débito";
                MovimentoFinanceiro estorno = new MovimentoFinanceiro(tipoEstorno, "Estorno de " + movimento.getTipo(),
                        Math.abs(movimento.getValor()), movimento.getData());
                movimentos.add(estorno);
                break;
            }
        }
    }

    public void visualizarMovimento(int codigo) {
        for (MovimentoFinanceiro movimento : movimentos) {
            if (movimento.getCodigo() == codigo) {
                System.out.println(movimento);
                return;
            }
        }
        System.out.println("Movimento não encontrado.");
    }

    public void exibirSaldo() {
        double saldo = 0.0;
        double creditos = 0.0;
        double debitos = 0.0;

        for (MovimentoFinanceiro movimento : movimentos) {
            double valor = movimento.getValor();
            if (movimento.getTipo().equals("Crédito")) {
                creditos += valor;
                saldo += valor;
            } else if (movimento.getTipo().equals("Débito")) {
                debitos += valor;
                saldo += valor;
            }
        }

        System.out.printf("Saldo: %.2f%n", saldo);
        System.out.printf("Créditos: %.2f%n", creditos);
        System.out.printf("Débitos: %.2f%n", debitos);
    }

    public void gerarRelatorio(String tipo, Date data) {
        double creditos = 0;
        double debitos = 0;

        System.out.println("Relatório de Movimentos:");
        for (MovimentoFinanceiro movimento : movimentos) {
            if (movimento.getTipo().equalsIgnoreCase(tipo) && movimento.getData().equals(data)) {
                System.out.printf("Código: %d, Tipo: %s, Descrição: %s, Valor: %.2f, Data: %s%n",
                        movimento.getCodigo(), movimento.getTipo(), movimento.getDescricao(),
                        Math.abs(movimento.getValor()), new SimpleDateFormat("yyyy-MM-dd").format(movimento.getData()));

                if (movimento.getTipo().equalsIgnoreCase("Crédito")) {
                    creditos += Math.abs(movimento.getValor());
                } else if (movimento.getTipo().equalsIgnoreCase("Débito")) {
                    debitos += Math.abs(movimento.getValor());
                }
            }
        }

        double saldo = creditos - debitos;
        System.out.printf("Saldo: %.2f%n", saldo);
        System.out.printf("Créditos: %.2f%n", creditos);
        System.out.printf("Débitos: %.2f%n", debitos);
    }

    public void gerarRelatorioMensal(String mesAnoInicial, String mesAnoFinal) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        java.util.Date dataInicialUtil = formatter.parse(mesAnoInicial);
        java.sql.Date dataInicial = new java.sql.Date(dataInicialUtil.getTime());

        java.util.Date dataFinalUtil = formatter.parse(mesAnoFinal);
        java.sql.Date dataFinal = new java.sql.Date(dataFinalUtil.getTime());

        Calendar cal = Calendar.getInstance();
        cal.setTime(dataFinal);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        dataFinal = new java.sql.Date(cal.getTime().getTime());

        Map<java.sql.Date, Double> saldosMensais = new TreeMap<>();
        Map<java.sql.Date, Double> creditosMensais = new TreeMap<>();
        Map<java.sql.Date, Double> debitosMensais = new TreeMap<>();

        for (MovimentoFinanceiro movimento : movimentos) {
            java.sql.Date dataMovimento = new java.sql.Date(movimento.getData().getTime());
            if ((dataMovimento.equals(dataInicial) || dataMovimento.after(dataInicial)) &&
                    (dataMovimento.equals(dataFinal) || dataMovimento.before(dataFinal))) {
                cal.setTime(dataMovimento);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                java.sql.Date mesAnoMovimento = new java.sql.Date(cal.getTime().getTime());
                saldosMensais.put(mesAnoMovimento,
                        saldosMensais.getOrDefault(mesAnoMovimento, 0.0) + movimento.getValor());
                if (movimento.getTipo().equals("Crédito")) {
                    creditosMensais.put(mesAnoMovimento,
                            creditosMensais.getOrDefault(mesAnoMovimento, 0.0) + movimento.getValor());
                } else if (movimento.getTipo().equals("Débito")) {
                    debitosMensais.put(mesAnoMovimento,
                            debitosMensais.getOrDefault(mesAnoMovimento, 0.0) + movimento.getValor());
                }
            }
        }

        System.out.println("Relatório Mensal:");
        for (Map.Entry<java.sql.Date, Double> entry : saldosMensais.entrySet()) {
            System.out.printf("Mês/Ano: %s, Saldo: %.2f, Créditos: %.2f, Débitos: %.2f%n",
                    formatter.format(entry.getKey()),
                    entry.getValue(),
                    creditosMensais.getOrDefault(entry.getKey(), 0.0),
                    debitosMensais.getOrDefault(entry.getKey(), 0.0));
        }
    }

    public void salvarEmArquivo(String nomeArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            for (MovimentoFinanceiro movimento : movimentos) {
                writer.write(String.format("%d,%s,%s,%.2f,%s%n",
                        movimento.getCodigo(), movimento.getTipo(), movimento.getDescricao(),
                        movimento.getValor(), movimento.getData()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void carregarDeArquivo(String nomeArquivo) {
        movimentos.clear(); 

        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd"); 
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                java.util.Date dataUtil = formatoData.parse(dados[4]); 
                java.sql.Date data = new java.sql.Date(dataUtil.getTime());
                MovimentoFinanceiro movimento = new MovimentoFinanceiro(
                        dados[1], dados[2], Double.parseDouble(dados[3]), data);
                movimentos.add(movimento);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

}
