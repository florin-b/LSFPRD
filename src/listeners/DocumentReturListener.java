package listeners;

import beans.BeanDocumentRetur;

public interface DocumentReturListener {
	public void documentSelected(String nrDocument);
	public void documentSelected(BeanDocumentRetur documentRetur);
}
