package br.com.jpsp.services;

import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Utils;

public class Strings {

	public static final String VERSION = "1.0.1";
	public static final String VERSION_DATE = "10/05/2024";

	public static final String START = "Iniciar";

	public static final String PAUSE = "Pausar ";

	public static final String JAVA_VERSION_ERROR = "Java versão " + Utils.JAVA_MIN_VERSION + " necessária para rodar esta aplicação (sua versão é " + Utils.JAVA_VERSION + ")";

	public static final String DESC_ORDER = "Ordem decrescente";
	public static final String ASC_ORDER = "Ordem crescente";
	public static final String NO_TASK = "Nenhuma atividade em execução";
	public static final String OTHER = "Geral";

	public static final String TODAY = "Hoje";

	public static final String JAN = "Janeiro";
	public static final String FEB = "Fevereiro";
	public static final String MAR = "Março";
	public static final String APR = "Abril";
	public static final String MAY = "Maio";
	public static final String JUN = "Junho";
	public static final String JUL = "Julho";
	public static final String AUG = "Agosto";
	public static final String SEP = "Setembro";
	public static final String OCT = "Outubro";
	public static final String NOV = "Novembro";
	public static final String DEZ = "Dezembro";

	public static final String APP_TITLE = "jPSP - java Personal Software Process";
	public static final String ABOUT = "Sobre...";
	public static final String GPL3 = "GPL-3.0 License";
	public static final String README = "README";

	public static final String LOADING = "Carregando, por favor, aguarde...";
	public static final String LOADING_MIGRATING_DB = "Migrando base de dados...";
	public static final String LOADING_CREATING_DB = "Criando base de dados...";
	public static final String LOADING_GENERATE_REPORT = "Gerando relatório...";
	public static final String MIGRATION_SUCCESS = "Base de dados migrada com sucesso.";

	public static final String MONDAY = "Segunda-feira";
	public static final String TUESDAY = "Terça-feira";
	public static final String WEDNESDAY = "Quarta-feira";
	public static final String THURSDAY = "Quinta-feira";
	public static final String FRIDAY = "Sexta-feira";
	public static final String SATURDAY = "Sábado";
	public static final String SUNDAY = "Domingo";

	public static final String SAGE = "SAGe";
	public static final String AGILIS = "Agilis";
	public static final String OTHER_SYS = "Outro";
	public static final String LOGS = "Logs";

	public static class Alert {
		public static final String TIME_REACHED = "Hora atingida";
		public static final String TIME_ALERT = "Alerta de horário";
		public static final String TITLE = "Alerta";

	}

	public static class jPSP {
		public static final String TITLE = "jPSP - Controle de tempo";

		public static final String EDIT = "Editar";
		public static final String INCLUDE = "Incluir";
		public static final String EXCLUDE = "Excluir";
		public static final String OPTIONS = "Opções";


		public static final String CRUD = "Editar...";
		public static final String EDIT_TASKS = "Atividades";
		public static final String EDIT_DESCS = "Descrições";
		public static final String EDIT_TASK_CLASS = "Classificações";
		public static final String EDIT_SYSTEMS = "Sistemas";


		public static final String SELECT_1_TASK = "Selecione apenas 1 atividade.";
		public static final String SELECT_1_OR_MORE_TASKS = "Selecione 1 ou mais atividades.";

		public static final String BATCH_EDIT_TASK_AND_DESC = "Editar atividade/descrição em lote";
		public static final String SELECT_2_OR_MORE_TASKS = "Selecione 2 ou mais atividades.";
		public static final String BATCH_EDIT_CLASSIFICATION = "Editar classificação em lote";
		public static final String BATCH_EDIT_SYSTEM = "Editar sistema em lote";

		public static final String SPLIT = "Dividir em 2 atividades";
		public static final String MERGE = "Fundir atividades";

		public static final String CONFIGURATIONS = "Configurações";

		public static final String DATA_BASE = "Banco de dados";
		public static final String REPORT = "Relatório";

		public static final String ABOUT = "Sobre o jPSP";

		public static final String TASK_TYPE = "Atividade";
		public static final String TASK_DESC = "Descrição da atividade";
		public static final String TASK_CLASSIFICATION = "Classificação";
		public static final String TASK_INIT_PAUSE = "Iniciar/Pausar";

		public static final String SEARCH = "Pesquisar";
		public static final String SEARCH_LABEL = "Pesquisar...";

		public static final String BY_DATE = "...por data";
		public static final String BY_TASK = "...por atividade";

		public static final String DAY_SUMMARY = "Resumo do dia";

		public static final String START = "Início";
		public static final String END = "Fim";

		public static final String HOURS_TOTAL = "Total horas";
		public static final String INTERVALS = "Intervalos";
		public static final String NO_TASK_IN_PROGRESS = "Nenhuma atividade em execução";
		public static final String IN_PROGRESS = "Em andamento";

		public static final String CHRONOMETER = "Cronômetro";

		public static final String SELECTION = "Seleção";

		public static final String LAST_TASK = "Última atividade";

		public static final String TASK_IN_PROGRESS_CONFIRM = "Existe uma atividade em andamento. Suspender esta atividade e sair?";


		public static final String TASK_IN_PROGRESS = "Atividade em andamento";

		public static final String CONTINUE_THIS_TASK = "Continuar esta atividade";

		public static final String CONFIRM_TASK_LIST_EXCLUSION = "Confirma exclusão das atividades selecionadas?";
		public static final String CONFIRM_TASK_EXCLUSION = "Confirma exclusão da atividade '&1'?";


		public static final String TASK_ALREADY_IN_PROGRESS = "Existe uma atividade em andamento. Para continuar a atividade '&1' pause a atividade '&2' primeiro.";

		public static final String TASK_STARTED_AT = "Atividade iniciada em &1 (&2)";

		public static final String DAY = "Dia";
		public static final String MONTH = "Mês";
		public static final String YEAR = "Ano";

		public static final String TASK_SPENT_TIME = "Tempo gasto na atividade";
		public static final String TASK_TOTAL_SPENT_TIME = "Tempo gasto na atividade '&1'";

		public static final String CREATING_DB = "Criando banco de dados, por favor aguarde...";


		public static final String TASK_ACTIVITY = "Atividade";
		public static final String TASK_DESCRIPTION = "Descrição";
		public static final String TASK_SYSTEM = "Sistema";
		public static final String TASK_CLASS = "Classificação";

		public static final String ERROR_BLOCKED = "Não é possível alterar/remover '&1' porque é um item bloqueado.";

		public static final String BLOCKED = "Bloqueado";


		public static final String COL_DATE = "Data";
		public static final String COL_WEEK_DAY = "Dia da Semana";
		public static final String COL_DELTA = "Delta";

	}

	public static class ConfigWindow {
		public static final String TITLE = "Configurações";
		public static final String AUTOMATICALLY_START_PAUSE = "Iniciar/Pausar automaticamente quando o sistema for bloqueado";
		public static final String RESTART_FROM_LAST_TASK = "Reiniciar automaticamente a última atividade quando a aplicação for aberta";
		public static final String ALERT_WHEN_TIME_REACHED = "Alertar quando chegar a hora";
		public static final String YOUR_NAME = "Seu nome";
		public static final String OUTPUT_FOLDER = "Diretório de saída";
		public static final String APPEARANCE = "Aparência";
		public static final String ERROR_OUTPUT_FOLDER = "Diretório de saída inválido ou inexistente '&1'.";
	}

	public static class SplitTasks {
		public static final String TITLE = "Dividir em 2 tarefas";

		public static final String TASK_1 = "Tarefa 1";
		public static final String TASK_2 = "Tarefa 2";

	}

	public static class Form {
		public static final String START = "Início";
		public static final String END = "Fim";
		public static final String DELTA = "Delta";
		public static final String TASK = "Atividade";
		public static final String DESCRIPTION = "Descrição";
		public static final String CLASSIFICATION = "Classificação";
		public static final String ERRORS = "Ocorreram os seguintes erros";

		public static final String FILL_MANDATORY_FIELDS = "É necessário preencher os campos:";
		public static final String MANDATORY_FIELDS = "Campos obrigatórios";

		public static final String EDIT = "Editar";
		public static final String EXCLUDE = "Excluir";
		public static final String INCLUDE = "Incluir";
		public static final String SYSTEM = "Sistema";
		public static final String CONFIRM_EXCLUSION = "Confirma exclusão de '&1'?";

		public static final String TITLE = "Editar &1";
		public static final String ERROR_SELECT_ITEM = "Selecione um item.";
		public static final String ERROR_MANDATORY_FIELD = "Por favor, preencha com um texto.";
		public static final String ITEM_TEXT = "Texto do item";

	}

	public static class MergeTasks {
		public static final String TITLE = "Fundir tarefas";
	}

	public static class GUI {
		public static final String ERROR = "Erro";
		public static final String CONFIRM_ACTION = "Confirmar ação";

		public static final String EXIT = "Sair";
		public static final String CANCEL = "Cancelar";
		public static final String CONFIRM = "Confirmar";

		public static final String SAVE = "Salvar";
		public static final String OK = "OK";
	}

	public static class DBOptions {
		public static final String TITLE = "Opções do Banco de Dados";
		public static final String OPTIONS = "Opções do Banco de Dados";
		public static final String BACKUP_DB = "Fazer backup do banco de dados";
		public static final String RESTORE_DB = "Restaurar banco de dados";
		public static final String EXPORT_DB = "Exportar dados para arquivo texto";
		public static final String IMPORT_DB = "Importar dados de arquivo texto";
		public static final String ERROR_BACKUP = "Erro ao fazer backup do arquivo '" + FilesUtils.DATABASE_FILE_V1 + "'";
		public static final String SUCCESS_BACKUP = "Backup da base de dados realizado com sucesso em '&1'.";
		public static final String EXPORT_TITLE = "Exportar banco de dados para arquivo texto";
		public static final String IMPORT_TITLE = "Importar banco de dados de arquivo texto";
		public static final String FILE_NAME = "Nome do arquivo";
		public static final String CHOOSE_DIR = "Escolher diretório...";
		public static final String CHOOSE_FILE = "Escolher arquivo...";
		public static final String SEPARATOR = "Separador";
		public static final String DATABASE_RELOADING = "Relendo base de dados, por favor, aguarde...";

		public static final String EXPORT = "Exportar";
		public static final String EXPORT_SUCCESS = "Arquivo salvo com sucesso em '&1'.";
		public static final String EXPORT_ERROR = "Não foi possível criar o arquivo '&1'.";
		public static final String DEFAULT_EXPORT_FILE_NAME = "jpsp_db_export.csv";
		public static final String INCLUDE_HEADERS = "Incluir cabeçalho";

		public static final String IMPORT = "Importar";
		public static final String IMPORT_SUCCESS = "Arquivo processado com sucesso: '&1'.";
		public static final String IMPORT_ERROR = "Não foi possível importar dados do arquivo '&1'.";
		public static final String IMPORT_ERROR_MESSAGE = "Base de dados foi restaurada com os dados antigos.";
		public static final String ENCODING = "Encoding";
		public static final String HAS_HEADERS = "Arquivo possui cabeçalho";
		public static final String IMPORT_WARNING = "ATENÇÃO! Faça backup do banco de dados antes de continuar.";
		public static final String CONFIRM_IMPORT = "Confirma importação de dados?";
		public static final String DELETE_ALL_DATA = "Apagar base de dados atual e incluir novos dados.";
		public static final String DO_NOT_DELETE_ALL_DATA = "Manter base de dados atual e acrescentar novos dados.";
		public static final String DELETE_ALL_DATA_CONFIRM = "Tem certeza de que deseja apagar a base de dados atual?";
		public static final String ERROR_SEPARATOR_REQUIRED = "Campo '" + SEPARATOR + "' é obrigatório.";
		public static final String ERROR_IMPORT_FILE_REQUIRED = "É obrigatório informar um arquivo para importar os dados.";
		public static final String IMPORTING_DATA = "Importando dados, por favor aguarde...";
		public static final String READING_FILE = "Lendo arquivo, por favor aguarde...";
		public static final String LINES_READ = "&1 linhas lidas do arquivo";
	}

	public static class BatchUpdateTask {
		public static final String TITLE_CLASS = "Editar classificações em lote";
		public static final String TITLE_DESC = "Editar descrições em lote";
		public static final String TITLE_SYSTEM = "Editar sistemas em lote";
		public static final String ACTIVITY = "Atividade";
		public static final String DESCRIPTION = "Descrição";
		public static final String CLASSIFICATION = "Classificação";
		public static final String PLEASE_ENTER_TASK = "Por favor, preencha a Atividade";
		public static final String PLEASE_ENTER_SYSTEM = "Por favor, preencha o Sistema";
		public static final String SYSTEM = "Sistema";
	}

	public static class IncludeOrUpdateTask {
		public static final String TITLE_INCLUDE = "Criar nova tarefa";
		public static final String TITLE_EDIT = "Alterar tarefa";
	}

	public static class RestoreDB {
		public static final String TITLE = "Restaurar backup de banco de dados";
		public static final String CURRENT_FILE = "Arquivo atual";
		public static final String TITLE_INCLUDE = "Criar nova tarefa";
		public static final String CHOOSE_FILE = "Escolher arquivo...";
		public static final String WARNING = "ATENÇÃO! Faça backup do banco de dados antes de continuar.";
		public static final String CONFIRM_RESTORE = "Confirma restauração do banco de dados?";
		public static final String SUCESS = "Banco de dados restaurado com sucesso.";
		public static final String ERROR = "Erro ao restaurar o banco de dados.";
		public static final String RESTORE = "Restaurar";
		public static final String NO_FILE_SELECTED = "Selecione um arquivo para restaurar.";
	}

	public static class Report {
		public static final String TITLE = "Gerar relatório mensal";
		public static final String HTML = "Gerar HTML";
		public static final String EXCEL = "Gerar Excel";
		public static final String TYPES = "Gráfico (por classificação)";
		public static final String SUMMARY = "Resumido";
		public static final String DETAILED = "Detalhado";
		public static final String DETAILED_GROUPED = "Detalhado (agrupado por dia)";
		public static final String TASK_CHART = "Gráfico (por atividades)";
		public static final String OPEN_DEFAULT_BROWSER = "Abrir no browser default";
		public static final String INCLUDE_GRAPH = "Incluir gráfico por classificação";
		public static final String INCLUDE_TASK_GRAPH = "Incluir gráficopor atividade";
		public static final String HTML_OPTIONS = "Opções HTML";
		public static final String GENERATED_FILE = "Arquivo gerado";
		public static final String SUCCESS = "Arquivo HTML gerado com sucesso em '&1'. O arquivo será aberto no browser default (se já estiver aberto, verifique se o relatório já foi mostrado).";
		public static final String ERROR = "Ocorreu um erro na geração do arquivo.";
		public static final String EXCEL_GENERATED = "Arquivo Excel gerado com sucesso em '&1'";
		public static final String CHART_TYPE = "Gráfico (por classificação)";
		public static final String REPORT = "Relatório";
		public static final String SELECT_ALL = "Selecionar tudo";
		public static final String MONTH = "Mês";
		public static final String YEAR = "Ano";
		public static final String DATE_PARAMS = "Parâmetros de Data";
		public static final String ORDER_BY = "Ordenação";
	}

	public static class Excel {
		public static final String DETAILED = "Detalhado";
		public static final String TITLE = "Apontamento de Atividades";
		public static final String CONSOLIDATED = "Consolidado";
		public static final String COL_KIND = "CR/Solic.";
		public static final String COL_SYSTEM = "Sistema";
		public static final String COL_DESENV = "Desenv.";
		public static final String COL_CORRECTION = "Correção";
		public static final String COL_OTHERS = "Outros";
		public static final String TASK_TYPE = "Tipo da atividade";
		public static final String DEVELOPMENT = "Desenvolvimento";
		public static final String CONFIG_OTHERS = "Configuração/Outros";
		public static final String COL_DATE = "Data";

		public static final String COL_START = "Início";
		public static final String COL_END = "Término";
		public static final String COL_INTERRUPTION = "Interr.";
		public static final String COL_HOURS = "Hr. Trab";
		public static final String COL_TASK = "CR/Solic.";
		public static final String COL_TYPE = "Tipo";
		public static final String COL_DESCRIPTION = "Descrição";
		public static final String TOTAL = "Total";

		public static final String NAME = "Nome";
		public static final String TIME = "Período";
		public static final String TABLES = "Tabelas";

	}

	public static class Chart {
		public static final String TITLE = "Trabalho por classificação (&1)";
	}

}
