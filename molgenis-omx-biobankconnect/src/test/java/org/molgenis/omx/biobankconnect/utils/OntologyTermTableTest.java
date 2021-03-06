package org.molgenis.omx.biobankconnect.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.Iterator;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.util.tuple.Tuple;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OntologyTermTableTest
{
	OntologyLoader loader;
	OntologyTermTable table;

	@BeforeMethod
	public void setUp() throws DatabaseException, TableException, OWLOntologyCreationException
	{
		loader = new OntologyLoader("ontology-test", new File("src/test/resources/test-ontology-loader.owl"));
		table = new OntologyTermTable(loader);
	}

	@Test
	public void getAllColumns() throws TableException
	{
		assertEquals(table.getAllColumns().size(), 9);
	}

	@Test
	public void getCount()
	{
		assertEquals(table.getCount(), 12);
	}

	@Test
	public void iterator()
	{
		Iterator<Tuple> it = table.iterator();
		assertTrue(it.hasNext());
		Tuple tuple0 = it.next();
		assertEquals(tuple0.get("ontologyTerm"), "Person label test");
		assertEquals(tuple0.get("entity_type"), "ontologyTerm");

		assertTrue(it.hasNext());
		Tuple tuple1 = it.next();
		assertEquals(tuple1.get("ontologyTerm"), "Person label test");
		assertEquals(tuple1.get("ontologyTermSynonym"), "People");
		assertEquals(tuple1.get("entity_type"), "ontologyTerm");

		assertTrue(it.hasNext());
		Tuple tuple2 = it.next();
		assertEquals(tuple2.get("ontologyTermSynonym"), "Strange childhood");

		assertTrue(it.hasNext());
		Tuple tuple3 = it.next();
		assertEquals(tuple3.get("ontologyTerm"), "Child");

		assertTrue(it.hasNext());
		Tuple tuple4 = it.next();
		assertEquals(tuple4.get("ontologyTermSynonym"), "Son");

		assertTrue(it.hasNext());
		Tuple tuple5 = it.next();
		assertEquals(tuple5.get("ontologyTermSynonym"), "Daughter");

		assertTrue(it.hasNext());
		Tuple tuple6 = it.next();
		assertEquals(tuple6.get("ontologyTerm"), "Parent");

		assertTrue(it.hasNext());
		Tuple tuple7 = it.next();
		assertEquals(tuple7.get("ontologyTermSynonym"), "Dad");

		assertTrue(it.hasNext());
		Tuple tuple8 = it.next();
		assertEquals(tuple8.get("ontologyTermSynonym"), "Father");
		assertEquals(tuple8.get("ontologyTermIRI"), "http://harmonization/test/owl/1.0.0#Father");
	}
}
