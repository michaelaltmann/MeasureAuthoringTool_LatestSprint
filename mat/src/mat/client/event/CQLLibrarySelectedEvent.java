package mat.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class CQLLibrarySelectedEvent.
 */
public class CQLLibrarySelectedEvent extends GwtEvent<CQLLibrarySelectedEvent.Handler> {
	
	/** The type. */
	public static GwtEvent.Type<CQLLibrarySelectedEvent.Handler> TYPE = 
		new GwtEvent.Type<CQLLibrarySelectedEvent.Handler>();
		
	/**
	 * The Interface Handler.
	 */
	public static interface Handler extends EventHandler {
		
		/**
		 * On CQLLibrary selected.
		 * 
		 * @param event
		 *            the event
		 */
		public void onLibrarySelected(CQLLibrarySelectedEvent event);
	}

	/** The Cql Library id. */
	private String cqlLibraryId;
	
	/** The Cql library name. */
	private String libraryName;
	
	/** The is editable. */
	private boolean isEditable;
	
	/** The is locked. */
	private boolean isLocked;
	
	/** The locked user id. */
	private String lockedUserId;
	
	/** The cql Librart version. */
	private String cqlLibraryVersion;
	

	/**
	 * Instantiates a new cql library selected event.
	 */
	public CQLLibrarySelectedEvent(String cqlLibraryId, String cqlLibraryVersion, String libraryName,  boolean isEditable,boolean isLocked,String lockedUserId) {
		this.cqlLibraryId = cqlLibraryId;
		this.cqlLibraryVersion = cqlLibraryVersion;
		this.libraryName = libraryName;
		this.isEditable = isEditable;
		this.isLocked = isLocked;
		this.lockedUserId = lockedUserId;
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(Handler handler) {
		handler.onLibrarySelected(this);
	}

	

	public String getCqlLibraryId() {
		return cqlLibraryId;
	}

	public void setCqlLibraryId(String cqlLibraryId) {
		this.cqlLibraryId = cqlLibraryId;
	}

	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}

	public String getCqlLibraryVersion() {
		return cqlLibraryVersion;
	}

	public void setCqlLibraryVersion(String cqlLibraryVersion) {
		this.cqlLibraryVersion = cqlLibraryVersion;
	}

	/**
	 * Checks if is editable.
	 * 
	 * @return true, if is editable
	 */
	public boolean isEditable() {
		return isEditable;
	}

	/**
	 * Gets the locked user id.
	 * 
	 * @return the locked user id
	 */
	public String getLockedUserId() {
		return lockedUserId;
	}

	/**
	 * Sets the locked user id.
	 * 
	 * @param lockedUserId
	 *            the new locked user id
	 */
	public void setLockedUserId(String lockedUserId) {
		this.lockedUserId = lockedUserId;
	}

	/**
	 * Sets the editable.
	 * 
	 * @param isEditable
	 *            the new editable
	 */
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}
	

	/**
	 * Checks if is locked.
	 * 
	 * @return true, if is locked
	 */
	public boolean isLocked() {
		return isLocked;
	}

	/**
	 * Sets the locked.
	 * 
	 * @param isLocked
	 *            the new locked
	 */
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

}
