import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Main {
    public static void main(String[] args) {
        ContaCorrente conta = new ContaCorrente();

        // Carregar dados do arquivo se existirem
        conta.carregarDeArquivo("Movimentos.txt");

        boolean sair = false;

        while (!sair) {
            System.out.println("1. Incluir Movimento");
            System.out.println("2. Estornar Movimento");
            System.out.println("3. Visualizar Movimento");
            System.out.println("4. Exibir Saldo");
            System.out.println("5. Gerar Relatório");
            System.out.println("6. Relatório Mensal");
            System.out.println("7. Sair");

            int escolha = Integer.parseInt(System.console().readLine());

            switch (escolha) {
                case 1:
                    System.out.println("Tipo (Crédito/Débito): ");
                    String tipo = System.console().readLine();
                    System.out.println("Descrição: ");
                    String descricao = System.console().readLine();
                    System.out.println("Valor(00.00): ");
                    double valor = Double.parseDouble(System.console().readLine());
                    System.out.println("Data (yyyy-MM-dd): ");
                    String dataString = System.console().readLine();
                    SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date dataUtil = null;
                    try {
                        dataUtil = formatoData.parse(dataString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    java.sql.Date data = new java.sql.Date(dataUtil.getTime());

                    conta.incluirMovimento(tipo, descricao, valor, data);
                    break;

                case 2:
                    System.out.println("Código do Movimento a Estornar: ");
                    int codigoEstorno = Integer.parseInt(System.console().readLine());
                    conta.estornarMovimento(codigoEstorno);
                    System.out.println("== Estorno Realizado ==");
                    break;

                case 3:
                    System.out.println("Código do Movimento a Visualizar: ");
                    int codigoVisualizar = Integer.parseInt(System.console().readLine());
                    conta.visualizarMovimento(codigoVisualizar);
                    break;

                case 4:

                    conta.exibirSaldo();
                    break;

                case 5:
                    System.out.println("Escolha o tipo de movimento (Crédito/Débito): ");
                    String tipoRelatorio = System.console().readLine();

                    System.out.println("Escolha a data (yyyy-MM-dd) ");
                    String dataRelatorio = System.console().readLine();

                    // Converte a string de data em um objeto java.util.Date
                    SimpleDateFormat formatoDataRelatorio = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date dataUtilR = null;
                    try {
                        dataUtilR = formatoDataRelatorio.parse(dataRelatorio);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Converte java.util.Date para java.sql.Date
                    java.sql.Date dataR = new java.sql.Date(dataUtilR.getTime());

                    conta.gerarRelatorio(tipoRelatorio, dataR);
                    break;

                case 6:
                System.out.println("Digite o mês/ano inicial (yyyy-MM): ");
                String mesAnoInicial = System.console().readLine();
            
                System.out.println("Digite o mês/ano final (yyyy-MM): ");
                String mesAnoFinal = System.console().readLine();
            
                try {
                    conta.gerarRelatorioMensal(mesAnoInicial, mesAnoFinal);
                } catch (ParseException e) {
                    System.out.println("Erro ao analisar as datas. Certifique-se de que elas estão no formato correto (yyyy-MM).");
                }
                break;

                case 7:
                    // Salvar dados no arquivo ao sair
                    conta.salvarEmArquivo("Movimentos.txt");
                    sair = true;
                    break;

                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        }
    }
}
