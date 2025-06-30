//
//  CardSetViewController.swift
//  MTGChatBot
//
//  Created by Marcus Shaffer on 6/26/25.
//

import Foundation

class CardSetViewController: ObservableObject {
    func getCardSetData(cardSetID: String) async throws -> CardSet {
        var endpoint = ""

        print("Card set ID: \(cardSetID)")

        if let encoded = cardSetID.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) {
            endpoint = "https://api.scryfall.com/sets/\(encoded)"
            print(endpoint)
        }

        guard let url = URL(string: endpoint) else { // Set endpoint to be of type url
            throw SetError.invalidURL
        }

        let (data, response) = try await URLSession.shared.data(from: url)

        guard let response: HTTPURLResponse = response as? HTTPURLResponse, response.statusCode == 200 else {
            throw SetError.invalidResponse
        }

        do {
            let decoder = JSONDecoder() // Decode the JSON
            decoder.keyDecodingStrategy = .convertFromSnakeCase // Helps convert the data from snake to camel case

            return try decoder.decode(CardSet.self, from: data)

        } catch {
            throw SetError.invalidData
        }
    }
}
