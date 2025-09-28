package io.luwian.core.persistence;

/** Transactional unit of work abstraction. */
public interface UnitOfWork extends AutoCloseable {
    void commit();

    void rollback();

    @Override
    void close();
}
