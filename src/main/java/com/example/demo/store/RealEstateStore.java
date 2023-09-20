package com.example.demo.store;

import com.example.demo.exception.RealEstateException;
import com.example.demo.exception.RealEstateNotFoundException;
import com.example.demo.model.Address;
import com.example.demo.model.RealEstate;
import com.example.demo.model.RealEstateType;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class RealEstateStore {

    private final String QUERY_GET_ALL = "SELECT re.id, re.area, re.commission, re.number_of_rooms, re.price, re.\"type\",a.apartment_number, a.city, a.floor, a.house_number, a.region, a.street FROM real_estate.real_estate re inner join real_estate.address a on re.address_id = a.id";
    private final String QUERY_GET_BY_ID = "SELECT re.id, re.area, re.commission, re.number_of_rooms, re.price, re.\"type\",a.apartment_number, a.city, a.floor, a.house_number, a.region, a.street FROM real_estate.real_estate re inner join real_estate.address a on re.address_id = a.id where re.id=?";
    private final String QUERY_GET_BY_ADDRESS = "SELECT re.id, re.area, re.commission, re.number_of_rooms, re.price, re.\"type\",a.apartment_number, a.city, a.floor, a.house_number, a.region, a.street FROM real_estate.real_estate re inner join real_estate.address a on re.address_id = a.id where 1=1";
    private final String QUERY_INSERT_REAL_ESTATE = "INSERT INTO real_estate (area, commission, number_of_rooms, price, \"type\",address_id) VALUES(?,?,?,?,?,?)";
    private final String QUERY_INSERT_ADDRESS = "INSERT INTO address (apartment_number, city, floor, house_number, region, street) VALUES(?,?,?,?,?,?)";
    private final String QUERY_DELETE_REAL_ESTATE = "DELETE FROM real_estate where id=?";
    private final String QUERY_DELETE_ADDRESS = "DELETE FROM address where id=?";
    private final String QUERY_SELECT_ADDRESS_ID = "SELECT address_id FROM real_estate where id=?";

    private final String QUERY_UPDATE_REAL_ESTATE = "UPDATE real_estate SET area=?, commission=?, number_of_rooms=?, price=?, \"type\"=? WHERE id=?";
    private final String QUERY_UPDATE_ADDRESS = "UPDATE address SET apartment_number=?, city=?, floor=?, house_number=?, region=?, street=? WHERE id=?";


    @Autowired
    DataSource realEstateDatasource;

    public List<RealEstate> getAll() {
        try (Connection connection = realEstateDatasource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(QUERY_GET_ALL);

            List<RealEstate> realEstateEntities = new ArrayList<>();
            while (resultSet.next()) {
                realEstateEntities.add(buildFromQueryResult(resultSet));
            }
            return realEstateEntities;
        } catch (SQLException e) {
            log.error("getAll query: {}, error: {}", QUERY_GET_ALL, e.getMessage());
            return null;
        }
    }


    public RealEstate getById(Long id) {
        try (Connection connection = realEstateDatasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY_GET_BY_ID);
            statement.setLong(1, id);

            ResultSet resultSet = null;

            if (statement.execute()) {
                resultSet = statement.getResultSet();
            } else {
                log.error("getById error: not found by id {}", id);
                return null;
            }
            boolean next = resultSet.next();
            if (!next) {
                log.error("getById error: not found by id {}", id);
                return null;
            }
            return buildFromQueryResult(resultSet);
        } catch (SQLException e) {
            log.error("getById error: {}", e.getMessage());
            return null;
        }
    }

    public List<RealEstate> getByAddress(Address address) {

        String query = QUERY_GET_BY_ADDRESS;
        List<Object> paramList = new ArrayList<>();
        if (address.getRegion() != null) {
            query += " and a.region=?";
            paramList.add(address.getRegion());
        }
        if (address.getCity() != null) {
            query += " and a.city=?";
            paramList.add(address.getCity());
        }
        if (address.getStreet() != null) {
            query += " and a.street=?";
            paramList.add(address.getStreet());
        }
        if (address.getHouseNumber() != null) {
            query += " and a.house_number=?";
            paramList.add(address.getHouseNumber());
        }
        if (address.getApartmentNumber() != null) {
            query += " and a.apartment_number=?";
            paramList.add(address.getApartmentNumber());
        }
        if (address.getFloor() != null) {
            query += " and a.floor=?";
            paramList.add(address.getFloor());
        }

        try (Connection connection = realEstateDatasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < paramList.size(); i++) {
                statement.setObject(i + 1, paramList.get(i));
            }
            ResultSet resultSet;

            if (statement.execute()) {
                resultSet = statement.getResultSet();
            } else {
                log.error("getByAddress error: not found by address {}", address);
                return null;
            }

            List<RealEstate> realEstateEntities = new ArrayList<>();
            while (resultSet.next()) {
                realEstateEntities.add(buildFromQueryResult(resultSet));
            }

            return realEstateEntities;
        } catch (SQLException e) {
            log.error("getByAddress error: {}", e.getMessage());
            return null;
        }
    }

    public void create(RealEstate realEstateEntity) throws RealEstateException {
        try (Connection connection = realEstateDatasource.getConnection()) {
            connection.setAutoCommit(false);
            
            
            PreparedStatement statementAddress = connection.prepareStatement(QUERY_INSERT_ADDRESS,Statement.RETURN_GENERATED_KEYS);
            statementAddress.setString(1, realEstateEntity.getAddress().getApartmentNumber());
            statementAddress.setString(2, realEstateEntity.getAddress().getCity());
            statementAddress.setInt(3, realEstateEntity.getAddress().getFloor());
            statementAddress.setString(4, realEstateEntity.getAddress().getHouseNumber());
            statementAddress.setString(5, realEstateEntity.getAddress().getRegion());
            statementAddress.setString(6, realEstateEntity.getAddress().getStreet());

            statementAddress.executeUpdate();

            ResultSet resultInsertAddress = statementAddress.getGeneratedKeys();
            Integer insertedId = null;
            if(resultInsertAddress.next()) {
                insertedId = resultInsertAddress.getInt(1);
            }

            if(insertedId == null) {
                connection.rollback();
                throw new RealEstateException("error creating real estate: can not create address");
            }

            PreparedStatement statementRealEstate = connection.prepareStatement(QUERY_INSERT_REAL_ESTATE);
            statementRealEstate.setDouble(1, realEstateEntity.getArea());
            statementRealEstate.setDouble(2, realEstateEntity.getCommission());
            statementRealEstate.setInt(3, realEstateEntity.getNumberOfRooms());
            statementRealEstate.setDouble(4, realEstateEntity.getPrice());
            statementRealEstate.setString(5, realEstateEntity.getType().name());
            statementRealEstate.setInt(6, insertedId);

            statementRealEstate.executeUpdate();
            
            connection.commit();

        } catch (SQLException e) {
            log.error("create query: {}, error: {}", QUERY_INSERT_REAL_ESTATE + ";" + QUERY_INSERT_ADDRESS, e.getMessage());
            throw new RealEstateException("error creating real estate: " + e.getMessage(), e.getCause());
        }
    }


    public void update(Long id, RealEstate realEstateEntity) throws RealEstateNotFoundException, RealEstateException {
        try (Connection connection = realEstateDatasource.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement selectStatement = connection.prepareStatement(QUERY_SELECT_ADDRESS_ID);
            selectStatement.setLong(1, id);

            selectStatement.execute();
            ResultSet resultSet = selectStatement.getResultSet();
            Integer addressId = null;
            if (resultSet.next()) {
                addressId = resultSet.getInt(1);
            }

            if(addressId == null) {
                connection.rollback();
                throw new RealEstateException("error updating real estate: address not found");
            }

            PreparedStatement realEstateUpdateStatement = connection.prepareStatement(QUERY_UPDATE_REAL_ESTATE);
            realEstateUpdateStatement.setDouble(1, realEstateEntity.getArea());
            realEstateUpdateStatement.setDouble(2, realEstateEntity.getCommission());
            realEstateUpdateStatement.setInt(3, realEstateEntity.getNumberOfRooms());
            realEstateUpdateStatement.setDouble(4, realEstateEntity.getPrice());
            realEstateUpdateStatement.setString(5, realEstateEntity.getType().name());

            realEstateUpdateStatement.setLong(6, id);

            int result = realEstateUpdateStatement.executeUpdate();

            if (result == 0) throw new RealEstateNotFoundException("real estate with id " + id + " not found");


            PreparedStatement addressUpdateStatement = connection.prepareStatement(QUERY_UPDATE_ADDRESS);

            addressUpdateStatement.setString(1, realEstateEntity.getAddress().getApartmentNumber());
            addressUpdateStatement.setString(2, realEstateEntity.getAddress().getCity());
            addressUpdateStatement.setInt(3, realEstateEntity.getAddress().getFloor());
            addressUpdateStatement.setString(4, realEstateEntity.getAddress().getHouseNumber());
            addressUpdateStatement.setString(5, realEstateEntity.getAddress().getRegion());
            addressUpdateStatement.setString(6, realEstateEntity.getAddress().getStreet());
            addressUpdateStatement.setLong(7, addressId);

            addressUpdateStatement.executeUpdate();

            connection.commit();

        } catch (SQLException e) {
            log.error("update query: {}, error: {}", QUERY_UPDATE_REAL_ESTATE, e.getMessage());
            throw new RealEstateException("error updating real estate: " + e.getMessage(), e.getCause());
        }
    }


    public void delete(Long id) throws RealEstateException, RealEstateNotFoundException {
        try (Connection connection = realEstateDatasource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement selectStatement = connection.prepareStatement(QUERY_SELECT_ADDRESS_ID);
            selectStatement.setLong(1, id);

            selectStatement.execute();
            ResultSet resultSet = selectStatement.getResultSet();
            Integer addressId = null;
            if (resultSet.next()) {
                addressId = resultSet.getInt(1);
            }


            PreparedStatement deleteRealEstateStatement = connection.prepareStatement(QUERY_DELETE_REAL_ESTATE);
            deleteRealEstateStatement.setLong(1, id);
            int result = deleteRealEstateStatement.executeUpdate();

            if (result == 0)
                throw new RealEstateNotFoundException("error delete: real estate with id " + id + " not found");

            if(addressId != null) {
                PreparedStatement deleteAddressStatement = connection.prepareStatement(QUERY_DELETE_ADDRESS);
                deleteAddressStatement.setLong(1, addressId);
                int resultAddress = deleteAddressStatement.executeUpdate();

                if (resultAddress == 0)
                    throw new RealEstateNotFoundException("error delete: address with id " + id + " not found");
            }

            connection.commit();
        } catch (SQLException e) {
            log.error("delete query: {}, error: {}", QUERY_DELETE_REAL_ESTATE, e.getMessage());
            throw new RealEstateException("error delete real estate: " + e.getMessage(), e.getCause());
        }
    }


    private RealEstate buildFromQueryResult(ResultSet resultSet) throws SQLException {
        return RealEstate.builder()
                .id((resultSet.getLong("id")))
                .area(resultSet.getDouble("area"))
                .commission(resultSet.getInt("commission"))
                .numberOfRooms(resultSet.getInt("number_of_rooms"))
                .price(resultSet.getDouble("price"))
                .type(RealEstateType.valueOf(resultSet.getString("type")))
                .address(
                        Address.builder()
                                .apartmentNumber(resultSet.getString("apartment_number"))
                                .city(resultSet.getString("city"))
                                .floor(resultSet.getInt("floor"))
                                .houseNumber(resultSet.getString("house_number"))
                                .region(resultSet.getString("region"))
                                .street(resultSet.getString("street"))
                                .build()
                ).build();
    }
}
