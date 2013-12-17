package org.molgenis.omx.biobankconnect.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.data.Entity;
import org.molgenis.data.EntitySource;
import org.molgenis.data.Repository;
import org.molgenis.data.UnknownEntityException;

public class PhenoToOmxConvertor extends AbstractOmxConvertor
{
	public PhenoToOmxConvertor(String studyName, String filePath) throws IOException
	{
		super(studyName, filePath);
	}

	@Override
	public void collectProtocolInfo(EntitySource entitySource) throws IOException
	{
		try
		{
			Repository<? extends Entity> repo = entitySource.getRepositoryByEntityName("Protocol");
			for (Entity entity : repo)
			{
				String name = entity.getString("name");
				if (!protocolFeatureLinks.containsKey(name))
				{
					protocolFeatureLinks.put(name, new ArrayList<String>());
				}
				if (!entity.getString("Features_name").isEmpty())
				{
					for (String featureName : entity.getString("Features_name").split(","))
					{
						protocolFeatureLinks.get(name).add(createFeatureIdentifier(featureName));
					}
				}
			}
		}
		catch (UnknownEntityException e)
		{
			System.out.println("Missing Protocol");
		}

	}

	@Override
	public void collectVariableInfo(EntitySource entitySource) throws IOException
	{
		Repository<? extends Entity> repo = entitySource.getRepositoryByEntityName("Measurement");
		for (Entity entity : repo)
		{
			String featureName = entity.getString("name");
			String description = entity.getString("description");
			String dataType = "string";
			if (!entity.getString("categories_name").isEmpty())
			{
				dataType = "categorical";

				if (!featureCategoryLinks.containsKey(featureName)) featureCategoryLinks.put(featureName,
						new ArrayList<UniqueCategory>());
				List<UniqueCategory> listOfCategories = featureCategoryLinks.get(featureName);
				for (String categoryName : entity.getString("categories_name").split(","))
				{
					UniqueCategory category = copyCategoryContent(categoryInfo.get(categoryName));
					category.setIdentifier(createCategoryIdentifier(featureName + "_" + category.getCode()));
					if (!listOfCategories.contains(category)) listOfCategories.add(category);
				}
				featureCategoryLinks.put(featureName, listOfCategories);
			}
			if (!variableInfo.containsKey(featureName))
			{
				UniqueVariable newVariable = new UniqueVariable(featureName, description, dataType);
				variableInfo.put(featureName, newVariable);
			}
		}
	}

	@Override
	public void collectCategoryInfo(EntitySource entitySource) throws IOException
	{
		Repository<? extends Entity> repo = entitySource.getRepositoryByEntityName("Category");
		for (Entity entity : repo)
		{
			String name = entity.getString("name");
			String description = entity.getString("description");
			String code = entity.getString("code_string");
			categoryInfo.put(name, new UniqueCategory(name, code, description));
		}
	}

	public UniqueCategory copyCategoryContent(UniqueCategory category)
	{
		return new UniqueCategory(category.getName(), category.getCode(), category.getLabel());
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		new PhenoToOmxConvertor(args[0], args[1]);
	}
}