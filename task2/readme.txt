Программа компилировалась в среде разработки IntelliJ Idea с помощью шага создания артифакта.
Инструкция по настройке среды разработки http://vichargrave.com/debugging-hadoop-applications-with-intellij/

Аргументами к программе выступали входная папка с данными, выходная папка с результатами, количество Reducer`ов
hadoop jar FinanceDataAnalysis.jar msu.bigdata.FinanceDataAnalysis wasb://financedata@bigdatamsu.blob.core.windows.net/ wasb:///financedata/output 4

Для автоматизации запусков был написан bash скрипт, который запускает задачи с разным числом Reducer`ов
(файл hadoop-run.sh)

Для объединения результатов использовалась команда
hadoop fs -getmerge /financedata/output finance.txt

Работу выполнил:
	Ковальчук Александр, 520 группа