package org.molgenis.search;

import java.util.List;

import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.framework.tupletable.TupleTable;

/**
 * Interface that a concrete SearchService must implement.
 * 
 * @author erwin
 * 
 */
public interface SearchService
{
	/**
	 * Check if a type exists in the index
	 * 
	 * @param documentType
	 * @return
	 */
	boolean documentTypeExists(String documentType);

	/**
	 * Insert or update entities in the index of a documentType
	 * 
	 * @param documentType
	 * @param entities
	 */
	void updateIndex(String documentType, Iterable<? extends Entity> entities);

	/**
	 * delete documents by Ids
	 * 
	 * @param documentType
	 * @param documentId
	 */
	void deleteDocumentByIds(String documentType, List<String> documentIds);

	/**
	 * update document by Id
	 * 
	 * @param documentType
	 * @param documentId
	 * @param updateScript
	 */
	void updateDocumentById(String documentType, String documentId, String updateScript);

	/**
	 * Index a TupleTable
	 * 
	 * @param documentType
	 *            , teh documentType name
	 * @param tupleTable
	 */
	void indexTupleTable(String documentType, TupleTable tupleTable);

	/**
	 * Search the index
	 * 
	 * @param request
	 * @return
	 */
	SearchResult search(SearchRequest request);

	SearchResult multiSearch(MultiSearchRequest request);

	/**
	 * Get the total hit count
	 * 
	 * @param documentType
	 * @param queryRules
	 * @return
	 */
	long count(String documentType, Query q);

	/**
	 * delete documentType from index
	 * 
	 * @param indexname
	 * @return boolean succeeded
	 */
	void deleteDocumentsByType(String documentType);

	void updateIndexTupleTable(String documentType, TupleTable tupleTable);
}
