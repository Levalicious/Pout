/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.commons.metadata;

/**
 * A MetadataEvaluationException is thrown any time a {@link LazyMetadataValue} fails to evaluate its value due to
 * an exception. The originating exception will be included as this exception's cause.
 */
public class MetadataEvaluationException extends RuntimeException {
    MetadataEvaluationException(Throwable cause) {
        super(cause);
    }
}
