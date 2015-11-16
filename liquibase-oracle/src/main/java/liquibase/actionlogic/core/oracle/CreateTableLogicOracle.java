package liquibase.actionlogic.core.oracle;

import liquibase.Scope;
import liquibase.action.Action;
import liquibase.action.core.CreateTableAction;
import liquibase.actionlogic.core.CreateTableLogic;
import liquibase.database.Database;
import liquibase.database.core.oracle.OracleDatabase;
import liquibase.structure.core.Column;
import liquibase.util.StringClauses;

import java.util.List;

public class CreateTableLogicOracle extends CreateTableLogic {
    @Override
    protected Class<? extends Database> getRequiredDatabase() {
        return OracleDatabase.class;
    }

    @Override
    protected StringClauses generateSql(CreateTableAction action, Scope scope) {
        StringClauses clauses = super.generateSql(action, scope);

        if (action.primaryKey != null) {
            String primaryKeyTablespace = action.primaryKey.tablespace;
            if (primaryKeyTablespace != null) {
                clauses.append("USING INDEX TABLESPACE "+primaryKeyTablespace);
            }
        }

        return clauses;
    }

    @Override
    protected StringClauses generateColumnSql(Column column, CreateTableAction action, Scope scope, List<Action> additionalActions) {
        StringClauses clauses = super.generateColumnSql(column, action, scope, additionalActions);

        String defaultValue = clauses.get(ColumnClauses.defaultValue);
        if (defaultValue != null && defaultValue.startsWith("GENERATED ALWAYS ")) {
            clauses.replace(ColumnClauses.defaultValue, defaultValue.replaceFirst("DEFAULT ", ""));
        }

        return clauses;
    }
}