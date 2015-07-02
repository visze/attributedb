package de.charite.compbio.attributedb.model;

import java.util.Iterator;

import de.charite.compbio.attributedb.model.score.Attribute;

public interface IScoreIterator extends Iterator<Attribute> {

	Attribute nextScore();

}
