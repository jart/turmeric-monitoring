package org.ebayopensource.turmeric.monitoring.aggregation.error.reader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

import org.ebayopensource.turmeric.monitoring.aggregation.CassandraConnectionInfo;
import org.ebayopensource.turmeric.monitoring.aggregation.ColumnFamilyReader;
import org.ebayopensource.turmeric.monitoring.aggregation.data.AggregationData;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractErrorCountsReader.
 */
public abstract class AbstractErrorCountsReader extends ColumnFamilyReader<String> {

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<String, AggregationData<String>> readData() {
      Map<String, AggregationData<String>> result = new HashMap<String, AggregationData<String>>();
      try {
         List<String> keysToRead = retrieveKeysInRange();
         MultigetSliceQuery<String, Long, String> multigetSliceQuery = HFactory.createMultigetSliceQuery(
                  connectionInfo.getKeyspace(), STR_SERIALIZER, LONG_SERIALIZER, STR_SERIALIZER);
         multigetSliceQuery.setColumnFamily(columnFamilyName);
         multigetSliceQuery.setKeys(keysToRead);
         multigetSliceQuery.setRange(startTime.getTime(), endTime.getTime(), false, ROWS_NUMBER_MAX_VALUE);
         QueryResult<Rows<String, Long, String>> queryResult = multigetSliceQuery.execute();
         if (queryResult != null) {
            for (Row<String, Long, String> row : queryResult.get()) {
               AggregationData<String> rowData = new AggregationData<String>(row.getKey());
               for (HColumn<Long, String> column : row.getColumnSlice().getColumns()) {
                  rowData.addColumn(column.getName(), column.getValue());
               }
               result.put(row.getKey(), rowData);
            }
         } else {

         }
      } catch (Exception e) {
         e.printStackTrace();
         if (e.getCause() != null) {
            e.getCause().printStackTrace();
         }
      }
      return result;
   }

   /** The column family name. */
   protected String columnFamilyName;

   /**
    * Instantiates a new abstract error counts reader.
    * 
    * @param startTime
    *           the start time
    * @param endTime
    *           the end time
    * @param connectionInfo
    *           the connection info
    */
   public AbstractErrorCountsReader(Date startTime, Date endTime, CassandraConnectionInfo connectionInfo) {
      super(startTime, endTime, connectionInfo);

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<String> retrieveKeysInRange() {
      List<String> rowKeys = new ArrayList<String>();
      try {
         RangeSlicesQuery<String, Long, String> rangeSlicesQuery = HFactory.createRangeSlicesQuery(
                  connectionInfo.getKeyspace(), STR_SERIALIZER, LONG_SERIALIZER, STR_SERIALIZER);
         rangeSlicesQuery.setColumnFamily(columnFamilyName);
         rangeSlicesQuery.setKeys(null, null);
         rangeSlicesQuery.setReturnKeysOnly();
         rangeSlicesQuery.setRange(startTime.getTime(), endTime.getTime(), false, ROWS_NUMBER_MAX_VALUE);
         rangeSlicesQuery.setRowCount(ROWS_NUMBER_MAX_VALUE);
         QueryResult<OrderedRows<String, Long, String>> result = rangeSlicesQuery.execute();
         OrderedRows<String, Long, String> orderedRows = result.get();

         for (Row<String, Long, String> row : orderedRows) {
            if (!row.getColumnSlice().getColumns().isEmpty()) {
               rowKeys.add(row.getKey());
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
         if (e.getCause() != null) {
            e.getCause().printStackTrace();
         }
      }

      return rowKeys;
   }

}
