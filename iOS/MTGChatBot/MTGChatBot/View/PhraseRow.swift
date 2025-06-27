//
//  PhraseRow.swift
//  MTGChatBot
//
//  Created by Marcus Shaffer on 6/26/25.
//  Custom View to make the phrases in the Phrase key sheet look nice

import SwiftUI

struct PhraseRow: View {
    let command: String
    let description: String

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(command)
                .font(.headline)
                .foregroundColor(.black)
            Text(description)
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
    }
}

#Preview {
    PhraseRow(command: "Hello", description: "World")
}
