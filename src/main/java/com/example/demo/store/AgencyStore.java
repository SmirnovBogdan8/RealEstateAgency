package com.example.demo.store;

import com.example.demo.exception.ContractException;
import com.example.demo.model.Contract;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class AgencyStore {

    private final String QUERY_CREATE = "INSERT INTO contracts(internal_id,real_estate_id,creation_date,approved,client_name,employee_name) VALUES(?,?,?,?,?,?)";
    private final String QUERY_FIND_BY_INTERNAL_ID = "SELECT * FROM contracts WHERE internal_id=?";
    private final String QUERY_APPROVE = "UPDATE contracts SET approved=? WHERE internal_id=?";
    private final String QUERY_FIND = "SELECT * FROM contracts where 1=1";
    @Autowired
    DataSource agencyDatasource;

    public void create(Contract contract) throws ContractException {
        try (Connection connection = agencyDatasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY_CREATE);
            statement.setObject(1, contract.getInternalId());
            statement.setLong(2, contract.getRealEstateId());
            statement.setTimestamp(3, contract.getCreationDate());
            statement.setObject(4, contract.getApproved());
            statement.setString(5, contract.getClientName());
            statement.setString(6, contract.getEmployeeName());
            int result = statement.executeUpdate();
            if (result == 0) throw new ContractException("error creating contract ");
        } catch (SQLException e) {
            log.error("create query: {}, error: {}", QUERY_CREATE, e.getMessage());
            throw new ContractException("error creating contract: " + e.getMessage(), e.getCause());
        }
    }


    public Contract findByInternalId(UUID id) throws ContractException {
        try (Connection connection = agencyDatasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY_FIND_BY_INTERNAL_ID);
            statement.setObject(1, id);

            ResultSet resultSet = null;

            if (statement.execute()) {
                resultSet = statement.getResultSet();
            } else {
                throw new ContractException("contract with internal id " + id + " not found");
            }
            boolean next = resultSet.next();
            if (!next) {
                throw new ContractException("contract with internal id " + id + " not found");
            }
            return buildFromQueryResult(resultSet);
        } catch (SQLException e) {
            log.error("findByInternalId error: {}", e.getMessage());
            return null;
        }
    }

    public void approve(UUID id) throws ContractException {
        try (Connection connection = agencyDatasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY_APPROVE);
            statement.setBoolean(1, true);
            statement.setObject(2, id);
            int result = statement.executeUpdate();

            if (result == 0) throw new ContractException("can not approve contract with id " + id);
        } catch (SQLException e) {
            log.error("approve error: {}", e.getMessage());
        }
    }

    public void disapprove(UUID id) throws ContractException {
        try (Connection connection = agencyDatasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY_APPROVE);
            statement.setBoolean(1, false);
            statement.setObject(2, id);
            int result = statement.executeUpdate();

            if (result == 0) throw new ContractException("can not disapprove contract with id " + id);
        } catch (SQLException e) {
            log.error("disapprove error: {}", e.getMessage());
        }
    }

    public List<Contract> find(Timestamp dateFrom, Timestamp dateTo, Long realEstateId, Boolean approved, String clientName, String employeeName) {
        String query = QUERY_FIND;
        List<Object> paramList = new ArrayList<>();
        if (dateFrom != null) {
            query += " and creation_date>=?";
            paramList.add(dateFrom);
        }
        if (dateTo != null) {
            query += " and creation_date<=?";
            paramList.add(dateTo);
        }
        if (realEstateId != null) {
            query += " and real_estate_id=?";
            paramList.add(realEstateId);
        }
        if (approved != null) {
            query += " and approved=?";
            paramList.add(approved);
        }
        if (clientName != null) {
            query += " and client_name=?";
            paramList.add(clientName);
        }
        if (employeeName != null) {
            query += " and employee_name=?";
            paramList.add(employeeName);
        }

        try (Connection connection = agencyDatasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < paramList.size(); i++) {
                statement.setObject(i + 1, paramList.get(i));
            }
            ResultSet resultSet;
            List<Contract> contractList = new ArrayList<>();

            if (statement.execute()) {
                resultSet = statement.getResultSet();
            } else {
                return contractList;
            }

            while (resultSet.next()) {
                contractList.add(buildFromQueryResult(resultSet));
            }

            return contractList;
        } catch (SQLException e) {
            log.error("find error: {}", e.getMessage());
            return null;
        }
    }

    private Contract buildFromQueryResult(ResultSet resultSet) throws SQLException {
        return Contract.builder()
                .internalId(resultSet.getObject("internal_id", UUID.class))
                .realEstateId(resultSet.getLong("real_estate_id"))
                .clientName(resultSet.getString("client_name"))
                .employeeName(resultSet.getString("employee_name"))
                .approved(resultSet.getObject("approved", Boolean.class))
                .creationDate(resultSet.getTimestamp("creation_date"))
                .build();
    }


}
